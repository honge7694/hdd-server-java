package kr.hhplus.be.server.order.application.interactor;

import kr.hhplus.be.server.coupon.model.Coupon;
import kr.hhplus.be.server.coupon.repository.CouponRepository;
import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.command.ProductItemCommand;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.usercoupon.repository.UserCouponRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PlaceOrderInteractorTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private PlaceOrderInteractor placeOrderInteractor;

    @Autowired
    private PlaceOrderFacade placeOrderFacade;

    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus_test")
            .withEnv("MYSQL_ROOT_PASSWORD", "1234")
            .withEnv("MAX_CONNECTIONS", "100");;

    @DynamicPropertySource
    public static void init(DynamicPropertyRegistry registry) {
        mysqlContainer.start();

        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    private Coupon coupon;
    private List<ProductItemCommand> items;
    private Address address;
    @Mock
    private PlaceOrderOutput placeOrderOutput;
    private Product product;

    @BeforeAll
    void setUp() {

        // 주소 생성
        address = new Address("city", "street", "zipcode");

        // 쿠폰 생성
        coupon = Coupon.create("testCoupon", "abc123", 1000, 1000, LocalDate.now(), LocalDate.now());
        couponRepository.save(coupon);

        // 유저 생성, coupon 등록
        for(int i = 1; i <= 100; i++) {
            User user = User.create(
                    "test",
                    "test"+i+"@email.com",
                    "test",
                    address
            );
            user.chargeBalance(1000000);
            User updatedUser = userRepository.save(user);
//            System.out.println("updatedUser.getId() + updatedUser.getEmail() = " + updatedUser.getId() + updatedUser.getEmail());
            UserCoupon userCoupon = UserCoupon.create(updatedUser.getId(), coupon.getId());
            userCouponRepository.save(userCoupon);
        }

        // 상품 생성
        product = new Product("test", "test", "brand", 1000, 1000);
        Product savedProduct = productRepository.save(product);

        items = new ArrayList<>();
        items.add(
                new ProductItemCommand(savedProduct.getId(), 1)
        );
    }

    @Test
    @DisplayName("[재고 감소 동시성 제어] - 동시에 100명의 유저가 100번의 주문")
    public void order_Concurrent100Users_AllSuccess() throws Exception {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.nanoTime();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    placeOrderFacade.orderWithRetry(
                            new PlaceOrderCommand(
                                    userId,
                                    null,
                                    "abc"+userId+ UUID.randomUUID(),
                                    items),
                            placeOrderOutput);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println("e = " + e);
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.nanoTime();
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        writeResultToCSV("decreaseQuantity", threadCount, successCount.get(), failCount.get(), elapsedMs);


        System.out.println("성공 수: " + successCount.get());
        System.out.println("실패 수: " + failCount.get());
        System.out.println("총 소요 시간(ms): " + elapsedMs);

        Product productResult = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(900, productResult.getStockQuantity());
    }

    private void writeResultToCSV(String strategy, int total, int success, int fail, long elapsedMs) {
        String fileName = "performance_results.csv";
        File file = new File(fileName);

        boolean fileExists = file.exists();
        try (FileWriter writer = new FileWriter(file, true)) {
            if (!fileExists) {
                writer.write("Strategy,TotalThreads,Success,Fail,ElapsedMs\n");
            }
            writer.write(String.format("%s,%d,%d,%d,%d\n",
                    strategy, total, success, fail, elapsedMs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}