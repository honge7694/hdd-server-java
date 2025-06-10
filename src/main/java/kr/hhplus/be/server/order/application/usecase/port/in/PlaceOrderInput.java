package kr.hhplus.be.server.order.application.usecase.port.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;

public interface PlaceOrderInput {
    void orderItemCommand(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput) throws JsonProcessingException;
}
