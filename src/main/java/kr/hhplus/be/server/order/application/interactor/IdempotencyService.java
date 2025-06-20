package kr.hhplus.be.server.order.application.interactor;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.order.application.usecase.port.out.OrderIdempotencyRepository;
import kr.hhplus.be.server.order.domain.OrderIdempotency;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final OrderIdempotencyRepository idempotencyRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndSave(String idempotencyId, Long userId) {
        try {
            OrderIdempotency orderIdempotency = OrderIdempotency.create(userId, idempotencyId);
            idempotencyRepository.saveAndFlush(orderIdempotency);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("이미 처리된 요청입니다.");
        }
    }
}
