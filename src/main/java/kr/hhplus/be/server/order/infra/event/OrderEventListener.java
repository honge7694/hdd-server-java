package kr.hhplus.be.server.order.infra.event;

import kr.hhplus.be.server.order.domain.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCacheEviction(OrderCompletedEvent event) {
        log.info("주문 완료, 캐시 제거를 시작합니다.");

        // 캐시
        Cache productCache = cacheManager.getCache("products");
        if (productCache == null) {
            log.warn("products 캐시를 찾을 수 없습니다. 캐시 제거를 건너뜁니다.");
            return;
        }

        event.productIds().forEach(productId -> {
            try {
                productCache.evict(productId);
                log.info("products::{} 캐시 제거 완료", productId);
            } catch (Exception e) {
                log.warn("products::{} 캐시 제거 실패", productId);
            }
        });

    }
}
