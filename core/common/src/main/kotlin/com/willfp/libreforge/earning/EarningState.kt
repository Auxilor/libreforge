package com.willfp.libreforge.earning

internal data class EarningState(
    var amount: Double = 0.0,

    /** Reset timestamp in epoch milliseconds, or 0 if no reset is scheduled. */
    var expiresAt: Long = 0,

    /** Last policy check timestamp in epoch milliseconds, or 0 if never checked. */
    var checkedAt: Long = 0
) {
    var limit: Double = 0.0

    val remaining: Double
        get() = (limit - amount).coerceAtLeast(0.0)

    /** Seconds until reset, rounded up, or -1 if no reset is scheduled. */
    val remainingSeconds: Long
        get() = if (expiresAt == 0L) -1 else ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0) + 999) / 1000

    fun reset() {
        amount = 0.0
        expiresAt = 0
    }

    fun applyPolicy(policy: EarningPolicy, time: Long) {
        val now = maxOf(time, checkedAt)

        if (policy.cron != null) {
            val lastCheck = maxOf(checkedAt, policy.cronChangedAt)

            if (policy.nextAfter(lastCheck)?.let { it <= now } == true) {
                reset()
            }

            expiresAt = policy.nextAfter(now) ?: 0
        } else {
            if (expiresAt != 0L && expiresAt <= now) {
                reset()
            }

            if (amount > 0) startTimer(policy, now)
        }

        limit = policy.limit
        checkedAt = now
    }

    fun startTimer(policy: EarningPolicy, now: Long) {
        if (policy.cron == null && expiresAt == 0L &&
            (policy.timerStart == TimerStart.FIRST_EARNING || amount >= policy.limit)
        ) {
            expiresAt = Math.addExact(now, policy.duration)
        }
    }

    fun encode(): String {
        return listOf(
            amount,
            expiresAt,
            checkedAt
        ).joinToString("|")
    }

    companion object {
        fun decode(raw: String): EarningState {
            if (raw.isEmpty()) {
                return EarningState()
            }

            val parts = raw.split('|')
            require(parts.size == 3) { "Invalid earning state" }

            return EarningState(
                amount = parts[0].toDouble(),
                expiresAt = parts[1].toLong(),
                checkedAt = parts[2].toLong()
            ).also {
                require(it.amount.isFinite() && it.amount >= 0 && it.expiresAt >= 0 && it.checkedAt >= 0)
            }
        }
    }
}
