package kr.hhplus.be.server.product.service;

import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.product.dto.ProductResponseDto;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("특정 상품 조회")
    public void getProduct() throws Exception {
        // given
        Product product = new Product("핸드폰", "전자제품", "갤럭시", 10000, 10);
        ReflectionTestUtils.setField(product, "id", 1L);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        ProductResponseDto dto = productService.getProduct(1L);

        // then
        Assertions.assertThat(dto.name()).isEqualTo(product.getName());
    }

    @Test
    @DisplayName("특정 상품 조회 실패 - 404")
    public void getProductNotFound() throws Exception {
        // when & then
        Assertions.assertThatThrownBy(() ->
                productService.getProduct(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("해당 상품이 존재하지 않습니다.");

    }

    @Test
    @DisplayName("상품 리스트 페이징 조회")
    public void getProducts() throws Exception {
        // given
        List<Product> products = List.of(
            new Product("핸드폰", "전자제품", "갤럭시", 10000, 10),
            new Product("핸드폰", "전자제품", "아이폰", 10001, 10)
        );
        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<ProductResponseDto> result = productService.getProducts(pageable);

        // then
        Assertions.assertThat(result.getContent()).hasSize(2);
        Assertions.assertThat(result.getContent().get(0)).isInstanceOf(ProductResponseDto.class);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
    }
}