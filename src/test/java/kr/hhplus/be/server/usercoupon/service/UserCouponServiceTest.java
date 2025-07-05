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
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EntityManager entityManager;

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

        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);

        // Redis
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    private Coupon coupon;
    private Address address;
    private List<Long> userIds;

    @BeforeAll
    void setUp() {
        // 쿠폰 생성
        coupon = Coupon.create("test", "abc", 100, 100, LocalDate.now(), LocalDate.now());
        coupon = couponRepository.save(coupon);

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
            user.chargeBalance(10000);
            User savedUser = userRepository.save(user);
            System.out.println("savedUser.getId() = " + savedUser.getId());
        }
        // Redis 비우기
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("동시에 300명의 유저가 쿠폰을 1000개 등록")
    public void registerCoupon_Concurrent100Users_AllSuccess() throws InterruptedException {

        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        long startTime = System.nanoTime();

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    userCouponService.registerUserCoupon(new UserCouponRequestDto(userId, coupon.getId()));
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

        List<UserCoupon> issued = userCouponRepository.findAll();
        Coupon updated = couponRepository.findById(coupon.getId()).orElseThrow();

        assertEquals(1000, issued.size());
        assertEquals(9000, updated.getQuantity());
        long endTime = System.nanoTime();

        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("성공 수: " + successCount.get());
        System.out.println("실패 수: " + failCount.get());
        System.out.println("총 소요 시간(ms): " + elapsedMs);

        // CSV 기록
        //writeResultToCSV("decreaseQuantity", threadCount, successCount.get(), failCount.get(), elapsedMs);
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

    @Test
    @DisplayName("선착순 쿠폰 발급 100개 100명 성공")
    public void registerCouponQueue_Concurrent100Users_AllSuccess() throws InterruptedException {
        // given
        int threadCount = 50;
        int initialQuantity = coupon.getQuantity();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 1; i <= threadCount; i++) {
            final Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    userCouponService.queueCouponRequest(userId, coupon.getId());
                } catch (Exception e) {
                    System.out.println("e = " + e + userId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        // Awaitility를 사용하여 최종 조건이 만족될 때까지 최대 20초간 기다립니다.
        Awaitility.await().atMost(40, TimeUnit.SECONDS).until(() -> {
            // DB에서 직접 쿠폰 수량을 다시 조회합니다.
            Coupon currentCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
            // 현재 수량이 (초기 수량 - 요청 수)와 같아질 때까지 기다립니다.
            return currentCoupon.getQuantity() == (initialQuantity - threadCount);
        });


        // 최종 검증
        Coupon finalCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        Assertions.assertEquals(50, finalCoupon.getQuantity());
    }


}