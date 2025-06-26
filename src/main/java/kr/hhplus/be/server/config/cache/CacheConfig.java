package kr.hhplus.be.server.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class CacheConfig {

    public enum CacheType {

        // 상품 : 1분 TTL
        PRODUCTS("products", 1, TimeUnit.MINUTES);

        private final String cacheName;
        private final int expireAfterWrite;
        private final TimeUnit timeUnit;

        CacheType(String cacheName, int expireAfterWrite, TimeUnit timeUnit) {
            this.cacheName = cacheName;
            this.expireAfterWrite = expireAfterWrite;
            this.timeUnit = timeUnit;
        }

        public CaffeineCache buildCache() {
            return new CaffeineCache(this.cacheName, Caffeine.newBuilder()
                    .expireAfterWrite(this.expireAfterWrite, this.timeUnit)
                    .maximumSize(1000)
                    .build());
        }
    }

    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
                .map(CacheType::buildCache)
                .collect(Collectors.toList());

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}
