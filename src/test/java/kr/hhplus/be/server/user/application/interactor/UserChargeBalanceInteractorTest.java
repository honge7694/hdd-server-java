package kr.hhplus.be.server.user.application.interactor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderMessageRepository;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderRepository;
import kr.hhplus.be.server.order.presentation.dto.OrderRequestDto;
import kr.hhplus.be.server.order.presentation.dto.ProductRequestDto;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.Address;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.presentation.dto.UserChargeBalanceRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class UserChargeBalanceInteractorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserChargeBalanceInteractor userChargeBalanceInteractor;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderMessageRepository orderMessageRepository;

    @PersistenceContext
    EntityManager em;

    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus_test")
            .withEnv("MYSQL_ROOT_PASSWORD", "1234");

    @DynamicPropertySource
    public static void init(DynamicPropertyRegistry registry) {
        mysqlContainer.start();

        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    private User testUser;
    private Product product;

    @BeforeEach
    void setUp() {
        // 유저 생성
        testUser = User.create(
                "test",
                "test@email.com",
                "1234",
                new Address("city", "street", "zipcode")
        );
        testUser = userRepository.save(testUser);

        // 상품 생성
        product = new Product("컴퓨터", "IT", "삼성", 10000, 100);
        product = productRepository.save(product);

    }

    @Test
    @DisplayName("유저 포인트 충전 후 주문 성공")
    void chargeUserBalance_order_success() throws Exception {
        // given
        Long userId = testUser.getId();
        int chargeAmount = 100000;

        int productPrice = product.getPrice();
        int quantity = 2;
        int totalPrice = productPrice * quantity;
        int expectedBalanceAfterCharge = chargeAmount - totalPrice;

        // 유저 request data
        UserChargeBalanceRequestDto userRequestDto = new UserChargeBalanceRequestDto(userId, chargeAmount);
        String userRequestBody = objectMapper.writeValueAsString(userRequestDto);

        // 주문 request data
        List<ProductRequestDto> productItems = List.of(new ProductRequestDto(product.getId(), quantity));
        OrderRequestDto orderRequestDto = new OrderRequestDto(userId, null, productItems);
        String orderRequestBody = objectMapper.writeValueAsString(orderRequestDto);

        // when
        // 유저 포인트 충전
        mockMvc.perform(post("/users/v1/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userRequestBody));

        // 주문 요청
        ResultActions orderResultActions = mockMvc.perform(post("/orders/v1/orderplace")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestBody));

        // then
        orderResultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.totalPrice").value(totalPrice));

        // 포인트 차감 검증
        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getBalance()).isEqualByComparingTo(expectedBalanceAfterCharge);

        // 재고 차감 검증
        Product orderedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(orderedProduct.getStockQuantity()).isEqualTo(product.getStockQuantity());
    }

    @Test
    @DisplayName("유저 포인트 충전 실패 - 유저 없음")
    void chargeUserBalance_fail_notFoundUser() throws Exception {
        // given
        Long notFoundUserId = 9999L;
        int chargeAmount = 5000;

        UserChargeBalanceRequestDto requestDto = new UserChargeBalanceRequestDto(notFoundUserId, chargeAmount);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        ResultActions resultActions = mockMvc.perform(post("/users/v1/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isNotFound());
    }
}