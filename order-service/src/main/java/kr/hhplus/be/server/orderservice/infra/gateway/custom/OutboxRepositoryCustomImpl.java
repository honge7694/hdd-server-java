package kr.hhplus.be.server.orderservice.infra.gateway.custom;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.orderservice.domain.OutboxEvent;
import kr.hhplus.be.server.orderservice.infra.gateway.entity.OutboxEventEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<OutboxEvent> findTop100ByOrderByCreatedAtAsc() {
        return em.createQuery("SELECT o FROM OutboxEventEntity o ORDER BY o.createdAt ASC", OutboxEventEntity.class)
                .setMaxResults(100)
                .getResultList()
                .stream()
                .map(OutboxEventEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(OutboxEvent event) {
        OutboxEventEntity entity = em.find(OutboxEventEntity.class, event.getId());
        if (entity != null) {
            em.remove(entity);
        }
    }
}
