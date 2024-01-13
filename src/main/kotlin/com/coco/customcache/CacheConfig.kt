package com.coco.customcache

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            setOf(
                defaultCacheFactoryBean().`object`,
                kReadTransactionCacheFactoryBean().`object`
            )
        )
        return cacheManager
    }

    @Bean
    fun defaultCacheFactoryBean(): ConcurrentMapCacheFactoryBean {
        return ConcurrentMapCacheFactoryBean().apply {
            this.setName("default")
        }
    }

    @Bean
    fun kReadTransactionCacheFactoryBean(): KReadTransactionCacheFactoryBean {
        return KReadTransactionCacheFactoryBean().apply {
            this.setName("cacheNameByAnnotation")
        }
    }
}