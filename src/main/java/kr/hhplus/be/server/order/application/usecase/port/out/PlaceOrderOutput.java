package kr.hhplus.be.server.order.application.usecase.port.out;

import kr.hhplus.be.server.order.application.usecase.port.result.PlaceOrderResult;

public interface PlaceOrderOutput {
    void ok (PlaceOrderResult placeOrderResult);
}
