package kr.hhplus.be.server.order.application.interactor;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceOrderFacade {

    private final PlaceOrderInteractor placeOrderInteractor;
    private final IdempotencyService idempotencyService;

    public void orderWithRetry(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput) {

        // 주문 중복 확인
        idempotencyService.checkAndSave(placeOrderCommand.idempotencyKey(), placeOrderCommand.userId());

        int maxRetries = 10;
        long waitTime = 300L;

        for (int i = 0; i < maxRetries; i++) {
            try {
                placeOrderInteractor.orderItemCommand(placeOrderCommand, placeOrderOutput);
                return;
            } catch (ObjectOptimisticLockingFailureException | OptimisticEntityLockException | JsonProcessingException e) {
                System.out.println("재시도 (" + (i + 1) + ")");
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {}

                if (i == maxRetries - 1) {
                    throw new RuntimeException("최대 재시도 횟수 초과" + e);
                }
            }
        }
    }
}
