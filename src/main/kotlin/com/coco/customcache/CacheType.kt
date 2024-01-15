package com.coco.customcache

enum class CacheType(val allowNullValues: Boolean) {
    SAMPLE(true);

    companion object {
        object Values {
            const val SAMPLE = "SAMPLE"
        }
    }
}