package kr.hhplus.be.server.productservice.product.service;


import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.productservice.product.dto.ProductResponseDto;
import kr.hhplus.be.server.productservice.product.model.Product;
import kr.hhplus.be.server.productservice.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, String> redisTemplate;

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

    public List<ProductResponseDto> getProductRanking(String date) {
        log.info("product의 ranking을 조회합니다.");

        // Redis 조회
        String redisKey = "order:product:ranking:" + date;
        Set<String> rankedProducts = redisTemplate.opsForZSet().reverseRange(redisKey, 0, -1);

        if (rankedProducts == null || rankedProducts.isEmpty()) {
            return Collections.emptyList();
        }

        // DB 조회를 위해 List 변환
        List<Long> productIds = rankedProducts.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // DB 조회
        Map<Long, Product> productMap = productRepository.findByIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        log.info(productMap.toString());

        return rankedProducts.stream()
                .map(productIdStr ->  {
                    Product product = productMap.get(Long.parseLong(productIdStr));
                    if (product == null) {
                        log.warn("랭킹에 존재하는 상품 ID {}를 DB에서 찾을 수 없습니다.", productIdStr);
                        return null;
                    }
                    return new ProductResponseDto(product);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
