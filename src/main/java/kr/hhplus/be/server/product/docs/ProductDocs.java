package kr.hhplus.be.server.product.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.product.dto.ProductResponseDto;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/products")
@Tag(name = "상품")
public interface ProductDocs {

    @Operation(
            summary = "상품 조회 API",
            description = "상품 조회"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class, example = """
                            {
                                "status": 200,
                                "message": "success",
                                "data": {
                                    "id": 1,
                                    "name": "샘플 상품",
                                    "category": "도서",
                                    "brand": "BOOKS",
                                    "price": 10000,
                                    "stockQuantity": 50
                                }
                            }
                        """))),
            @ApiResponse(responseCode = "404", description = "해당 상품이 존재하지 않습니다.",
                content = @Content(
                        schema = @Schema(implementation = ApiResponse.class, example = """
                                {
                                    "status": 404,
                                    "message": "해당 상품이 존재하지 않습니다.",
                                    "data": null
                                }
                        """))),
    })
    @GetMapping("/v1/{id}")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<ProductResponseDto>> getProduct(
            @Parameter(name = "id", description = "상품 ID", required = true, example = "1")
            long id
    );

    @Operation(
            summary = "상품 목록 조회 API",
            description = "상품 목록을 페이지네이션하여 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class,
                                    example = """
                                            {
                                                "status": 200,
                                                "message": "success",
                                                "data": {
                                                    "content": [
                                                        {
                                                            "id": 1,
                                                            "name": "샘플 상품1",
                                                            "category": "도서",
                                                            "brand": "BOOKS",
                                                            "price": 10000,
                                                            "stockQuantity": 50
                                                        },
                                                        {
                                                            "id": 2,
                                                            "name": "샘플 상품2",
                                                            "category": "전자제품",
                                                            "brand": "Apple",
                                                            "price": 1350000,
                                                            "stockQuantity": 15
                                                        }
                                                    ],
                                                    "pageable": {
                                                        "sort": {
                                                            "empty": true,
                                                            "sorted": false,
                                                            "unsorted": true
                                                        },
                                                        "offset": 0,
                                                        "pageNumber": 0,
                                                        "pageSize": 10,
                                                        "paged": true,
                                                        "unpaged": false
                                                    },
                                                    "totalPages": 1,
                                                    "totalElements": 2,
                                                    "last": true,
                                                    "size": 20,
                                                    "number": 0,
                                                    "sort": {
                                                        "empty": true,
                                                        "sorted": false,
                                                        "unsorted": true
                                                    },
                                                    "first": true,
                                                    "numberOfElements": 2,
                                                    "empty": false
                                                }
                                            }
                                            """
                            )
                    )
            ),
    })
    @GetMapping("/v1")
    ResponseEntity<kr.hhplus.be.server.global.response.ApiResponse<Page<ProductResponseDto>>> getProducts(
            @ParameterObject Pageable pageable
    );
}
