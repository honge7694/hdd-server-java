package kr.hhplus.be.server.product.service;


import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.product.dto.ProductResponseDto;
import kr.hhplus.be.server.product.model.Product;
import kr.hhplus.be.server.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(cacheNames = "products", key = "#productId")
    public ProductResponseDto getProduct(long productId) {
        log.info("DB에서 쿠폰 조회 : {}", productId);
        return productRepository.findById(productId)
                .map(ProductResponseDto::new)
                .orElseThrow(() -> new NotFoundException("해당 상품이 존재하지 않습니다."));
    }

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponseDto> dtoList = productPage.getContent().stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }
}
