package net.romangr.propose_post_bot.regulation

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


@Component
class RequestCounter {
    private val requestCount: LoadingCache<Long, AtomicInteger> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .build(CacheLoader.from { _: Long? -> AtomicInteger() })

    fun incrementAndGet(telegramUserId: Long): Int = requestCount.get(telegramUserId).incrementAndGet()
}