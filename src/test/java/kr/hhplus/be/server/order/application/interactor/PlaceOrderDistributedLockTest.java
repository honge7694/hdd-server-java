package kr.hhplus.be.server.order.application.interactor;

import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.command.ProductItemCommand;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderRepository;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class PlaceOrderDistributedLockTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlaceOrderFacade placeOrderFacade;

    @Mock
    private PlaceOrderOutput placeOrderOutput;

    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus_test")
            .withEnv("MYSQL_ROOT_PASSWORD", "1234");


    @Container
    static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    public static void init(DynamicPropertyRegistry registry) {
        mysqlContainer.start();
        redisContainer.start();

        // MySQL
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.hikari.maximum-pool-size", () -> 100);

        // Redis
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379).toString());

        // Redisson
        String redisUrl = String.format("redis://%s:%d", redisContainer.getHost(), redisContainer.getMappedPort(6379));
        registry.add("spring.redisson.address", () -> redisUrl);
    }

    private Address address;
    private PlaceOrderCommand placeOrderCommand;
    private Product product;
    private List<ProductItemCommand> items;

    @BeforeAll
    void setUp() {
        // 주소 생성
        address = new Address("city", "street", "zipcode");
        // 유저 생성
        for(int i = 1; i <= 100; i++) {
            User user = User.create(
                    "test",
                    "test"+i+"@email.com",
                    "test",
                    address
            );
            user.chargeBalance(1000000);
            userRepository.save(user);

            Product products = new Product("test"+i, "test", "brand", 1000, 1000);
            productRepository.save(products);
        }
        // 상품 생성
//        product = new Product("test", "test", "brand", 1000, 1000);
//        Product savedProduct = productRepository.save(product);
    }

    @Test
    @DisplayName("redisson을 이용하여 유저 중복주문 분산락 적용")
    public void distributedLock_PlaceOrder() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) 1;
            final Long productId = (long) i;
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    items = new ArrayList<>();
                    items.add(
                            new ProductItemCommand(productId, 1)
                    );
                    placeOrderFacade.orderWithRetry(
                            new PlaceOrderCommand(
                                    userId,
                                    null,
                                    "abc"+userId+ UUID.randomUUID(),
                                    items
                            ), placeOrderOutput);
                } catch (Exception e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 스레드들이 생성되고 await() 상태까지 대기
        Thread.sleep(100);
        startLatch.countDown();

        endLatch.await();
        executorService.shutdown();

        // then
        int order = orderRepository.findAllSize();
        assertEquals(1, order, "중복 주문 요청시 1개만 성공");
    }
}
