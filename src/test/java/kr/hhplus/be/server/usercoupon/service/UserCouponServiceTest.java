package kr.hhplus.be.server.usercoupon.service;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.coupon.model.Coupon;
import kr.hhplus.be.server.coupon.repository.CouponRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.usercoupon.dto.UserCouponRequestDto;
import kr.hhplus.be.server.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.usercoupon.repository.UserCouponRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class UserCouponServiceTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus_test")
            .withEnv("MYSQL_ROOT_PASSWORD", "1234");
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private EntityManager em;

    @DynamicPropertySource
    public static void init(DynamicPropertyRegistry registry) {
        mysqlContainer.start();

        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    private Coupon coupon;
    private Address address;

    @BeforeAll
    void setUp() {
        // 쿠폰 생성
        coupon = Coupon.create("test", "abc", 10000, 100, LocalDate.now(), LocalDate.now());
        couponRepository.save(coupon);

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
            userRepository.save(
                user
            );
        }
    }

    @Test
    @DisplayName("동시에 100명의 유저가 쿠폰을 100개 등록")
    public void registerCoupon_Concurrent100Users_AllSuccess() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    userCouponService.registerUserCoupon(new UserCouponRequestDto(userId, coupon.getId()));
                } catch (Exception e) {
                    System.out.println("e = " + e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<UserCoupon> issued = userCouponRepository.findAll();
        Coupon updated = couponRepository.findById(coupon.getId()).orElseThrow();

        assertEquals(100, issued.size());
        assertEquals(0, updated.getQuantity());
    }
}