package kr.hhplus.be.server.order.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.order.usecase.in.ProductItemCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @Schema(description = "구매 상품 번호", example = "1")
    private Long productId;

    @Schema(description = "구매 상품 수량", example = "1")
    private int quantity;

    public ProductItemCommand toDomain() {
        return new ProductItemCommand(productId, quantity);
    }
}
