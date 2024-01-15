package com.coco.customcache

import org.springframework.beans.factory.BeanNameAware
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.transaction.interceptor.TransactionAspectSupport
import org.springframework.util.StringUtils


class KReadTransactionCacheFactoryBean : FactoryBean<TransactionScopedCache?>, BeanNameAware,
    InitializingBean {
    private var name = ""
    private var allowNullValues = true
    private var cache: TransactionScopedCache? = null

    /**
     * Specify the name of the cache.
     *
     * Default is "" (empty String).
     */
    fun setName(name: String) {
        this.name = name
    }

    /**
     * Set whether to allow `null` values
     * (adapting them to an internal null holder value).
     *
     * Default is "true".
     */
    fun setAllowNullValues(allowNullValues: Boolean) {
        this.allowNullValues = allowNullValues
    }

    override fun setBeanName(beanName: String) {
        if (!StringUtils.hasLength(name)) {
            setName(beanName)
        }
    }

    override fun afterPropertiesSet() {
        cache = TransactionScopedCache(name, allowNullValues)
    }

    override fun getObject(): TransactionScopedCache? {
        return cache
    }

    override fun getObjectType(): Class<*> {
        return TransactionScopedCache::class.java
    }

    override fun isSingleton(): Boolean {
        return false
    }
}