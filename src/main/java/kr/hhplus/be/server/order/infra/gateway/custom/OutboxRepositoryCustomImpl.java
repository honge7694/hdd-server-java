package kr.hhplus.be.server.order.infra.gateway.custom;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.order.domain.OutboxEvent;
import kr.hhplus.be.server.order.infra.gateway.entity.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryCustomImpl implements OutboxRepositoryCustom{

    private final EntityManager em;

    @Override
    public OutboxEvent findByOrderId(Long orderId) {
        OutboxEventEntity entity = em.find(OutboxEventEntity.class, orderId);
        if (entity == null) {
            throw new NotFoundException("주문을 찾을 수 없습니다. (orderId = " + orderId + ")");
        }
        return entity.toDomain(entity); // entity → 도메인 객체 변환
    }
}
