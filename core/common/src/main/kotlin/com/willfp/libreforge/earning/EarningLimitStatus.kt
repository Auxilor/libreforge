package com.willfp.libreforge.earning

data class EarningLimitStatus(
    val earned: Double,
    val remaining: Double,
    val resetAt: Long
) {
    val remainingSeconds: Long
        get() = if (resetAt == 0L) -1 else ((resetAt - System.currentTimeMillis()).coerceAtLeast(0) + 999) / 1000
}
