package kr.hhplus.be.server.product.controller;

import kr.hhplus.be.server.global.response.ApiResponse;
import kr.hhplus.be.server.product.docs.ProductDocs;
import kr.hhplus.be.server.product.dto.ProductResponseDto;
import kr.hhplus.be.server.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductDocs {

    private final ProductService productService;

    @Override
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable("id") long id) {
        return ResponseEntity.status(200)
                .body(ApiResponse.success(productService.getProduct(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<Page<ProductResponseDto>>> getProducts(Pageable pageable) {
        return ResponseEntity.status(200)
                .body(ApiResponse.success(productService.getProducts(pageable)));
    }
}
