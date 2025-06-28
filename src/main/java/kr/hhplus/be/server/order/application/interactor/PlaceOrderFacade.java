package kr.hhplus.be.server.order.application.interactor;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.order.application.usecase.command.PlaceOrderCommand;
import kr.hhplus.be.server.order.application.usecase.port.out.PlaceOrderOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceOrderFacade {

    private final PlaceOrderInteractor placeOrderInteractor;
    private final RedissonClient redissonClient;

    public void orderWithRetry(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput) {
        // 분산락을 통해 유저당 1개의 주문만 가능(주문 중복 방지)
        String lockKey = "lock:order:user"+placeOrderCommand.userId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(5, TimeUnit.SECONDS);

            if (!isLocked) {
                log.error("유저 중복 주문 요청 감지, lockKey : {}", lockKey);
                throw new IllegalStateException("이미 주문을 처리 중 입니다.");
            }
            executeWithOptimisticLockRetry(placeOrderCommand, placeOrderOutput);
        } catch (InterruptedException e) {
            log.error("주문 처리 중 오류가 발생하였습니다.");
            throw new RuntimeException(e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void executeWithOptimisticLockRetry(PlaceOrderCommand placeOrderCommand, PlaceOrderOutput placeOrderOutput) {
        int maxRetries = 10;
        long waitTime = 300L;

        // 낙관적락 재시도
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
