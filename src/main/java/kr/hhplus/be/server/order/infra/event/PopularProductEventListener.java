package kr.hhplus.be.server.order.infra.event;

import kr.hhplus.be.server.order.domain.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularProductEventListener {

    private final RedisTemplate<String, String> redisTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductIncreaseRanking(OrderCompletedEvent event) {
        log.info("주문 완료, Redis, 상품 랭킹 점수를 증가시킵니다.");

        // 키 생성
        String dailyRankingKey  = "order:product:ranking:" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);;

        event.productIds().forEach(productId -> {
            try {
                // 없으면 생성, 있으면 1씩 증가
                redisTemplate.opsForZSet().incrementScore(dailyRankingKey , String.valueOf(productId), 1);
            } catch (Exception e) {
                log.warn("상품 랭킹 점수 업데이트 실패 - productId : {}, error: {}", productId, e.getMessage());
            }
        });

        // 3일 뒤 삭제
        redisTemplate.expire(dailyRankingKey, 3, TimeUnit.DAYS);
    }
}
