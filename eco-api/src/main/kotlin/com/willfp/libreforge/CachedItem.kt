package com.willfp.libreforge

data class CachedItem<T>(
    val item: T,
    val expiry: Long
)