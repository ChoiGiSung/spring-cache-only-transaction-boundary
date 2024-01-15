package com.coco.customcache

import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.io.Serializable
import java.util.concurrent.Callable


/**
 * The cache in the transaction.
 * It is a local cache and the life cycle is the same as a transaction.
 * It is similar to local cache in mybatis, but the difference is that the contents of
 * the cache do not disappear when you insert, update, or delete.
 *
 * origin source: https://stackoverflow.com/questions/49695413/spring-cache-binded-only-to-the-current-transaction
 */
class TransactionScopedCache(
    private val name: String,
    private val isAllowNullValues: Boolean = true
) : Cache {
    /**
     * Create a new Map with the specified name and the
     * given internal ConcurrentMap to use.
     * @param name the name of the cache
     * @param isAllowNullValues whether to allow `null` values
     * (adapting them to an internal null holder value)
     */

    override fun getName(): String {
        return this.name
    }

    override fun getNativeCache(): Any {
        return getBindedCache(name);
    }

    override fun get(key: Any): Cache.ValueWrapper? {
        val bindedCache = getBindedCache(
            name
        )
        val value = bindedCache[key]
        return if (value != null) SimpleValueWrapper(fromStoreValue(value)) else null
    }

    override fun <T : Any?> get(key: Any, type: Class<T>?): T? {
        val bindedCache = getBindedCache(name) ?: return null
        val value = bindedCache[key]
        return if (value != null) {
            fromStoreValue(value) as T
        } else {
            null
        }
    }

    override fun <T : Any?> get(key: Any, valueLoader: Callable<T>): T? {
        val bindedCache = getBindedCache(name) ?: return null
        val value = bindedCache[key]
        return if (value != null) {
            fromStoreValue(value) as T
        } else {
            try {
                valueLoader.call()
            } catch (ex: Exception) {
                null
            }
        }
    }

    override fun put(key: Any, value: Any?) {
        val bindedCache = getBindedCache(
            name
        )
        bindedCache[key] = toStoreValue(value)
    }

    override fun evict(key: Any) {
        val bindedCache = getBindedCache(
            name
        )
        bindedCache.remove(key)
    }

    override fun clear() {
        val bindedCache = getBindedCache(
            name
        )
        bindedCache.clear()
    }

    /**
     * Convert the given value from the internal store to a user value
     * returned from the get method (adapting `null`).
     * @param storeValue the store value
     * @return the value to return to the user
     */
    protected fun fromStoreValue(storeValue: Any): Any? {
        return if (isAllowNullValues && storeValue === NULL_HOLDER) {
            null
        } else storeValue
    }

    /**
     * Convert the given user value, as passed into the put method,
     * to a value in the internal store (adapting `null`).
     * @param userValue the given user value
     * @return the value to store
     */
    private fun toStoreValue(userValue: Any?): Any? {
        return if (isAllowNullValues && userValue == null) {
            NULL_HOLDER
        } else userValue
    }

    private fun getBindedCache(name: String): MutableMap<Any, Any?> {
        return if (TransactionSynchronizationManager.isActualTransactionActive()) {
            var cache: MutableMap<Any, Any?>? =
                TransactionSynchronizationManager.getResource(name) as MutableMap<Any, Any?>?
            if (cache == null) {
                cache = mutableMapOf()
                TransactionSynchronizationManager.bindResource(name, cache)
                TransactionScopedCacheSynchronization(this).register()
            }
            cache
        } else {
            mutableMapOf()
        }
    }

    private class NullHolder : Serializable
    companion object {
        private val NULL_HOLDER: Any = NullHolder()
    }
}