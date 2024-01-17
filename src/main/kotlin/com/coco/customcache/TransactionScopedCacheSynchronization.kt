package com.coco.customcache

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

/**
 *   It should be used when using the thread pool.
 *   Delete the cache before it is returned because the TransactionScopedCache is stored in thread local.
 */
class TransactionScopedCacheSynchronization(private val cache: TransactionScopedCache) : TransactionSynchronization {

    override fun beforeCompletion() {
        super.beforeCompletion()
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.unbindResource(cache.name)
        }
    }

    fun register() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            if (TransactionSynchronizationManager.getSynchronizations().none { it::class.java == this::class.java }) {
                TransactionSynchronizationManager.registerSynchronization(this)
            }
        }
    }
}
