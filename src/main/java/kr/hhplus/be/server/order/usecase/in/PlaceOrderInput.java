package kr.hhplus.be.server.order.usecase.in;

import kr.hhplus.be.server.order.usecase.out.PlaceOrderOutput;

public interface PlaceOrderInput {
    void orderItemCommand(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput);
}
