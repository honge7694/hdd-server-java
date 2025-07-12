package kr.hhplus.be.server.order.application.usecase.port.result;

import kr.hhplus.be.server.order.application.usecase.port.result.dto.ProductItem;

import java.util.List;

public record OrderData (
    Long orderId,
    List<ProductItem> productList
) {}
