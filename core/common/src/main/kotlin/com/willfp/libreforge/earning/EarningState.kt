package com.willfp.libreforge.earning

import java.util.Base64

internal data class EarningState(
    var amount: Double = 0.0,
    /** Reset timestamp in epoch milliseconds, or 0 if no reset is scheduled. */
    var expiresAt: Long = 0,
    var resetType: ResetType? = null,
    var signature: String = "",
    var checkedAt: Long = 0,
    var session: String = "",
    var revision: Int = -1,
    var limit: Double = 0.0
) {
    val remaining: Double
        get() = (limit - amount).coerceAtLeast(0.0)

    /** Seconds until reset, rounded up, or -1 if no reset is scheduled. */
    val remainingSeconds: Long
        get() = if (expiresAt == 0L) -1 else ((expiresAt - System.currentTimeMillis()).coerceAtLeast(0) + 999) / 1000

    fun reset() {
        amount = 0.0
        expiresAt = 0
    }

    fun advance(
        policy: EarningPolicy,
        time: Long,
        startup: Boolean = false,
        transition: Boolean = false
    ) {
        val now = maxOf(time, checkedAt)
        val changed = resetType != policy.resetType || signature != policy.signature

        if (resetType == null) {
            checkedAt = now
        } else if (resetType != policy.resetType) {
            expiresAt = 0
            checkedAt = now
        } else if (changed && policy.cron != null && !startup) {
            checkedAt = now
        }

        if (policy.cron != null) {
            if (policy.nextAfter(checkedAt)?.let { it <= now } == true) {
                reset()
            }

            expiresAt = policy.nextAfter(now) ?: 0
        } else {
            if (expiresAt != 0L && expiresAt <= now) {
                reset()
            }

            if ((changed || transition || limit != policy.limit) && expiresAt == 0L && amount > 0 && amount >= policy.limit &&
                policy.timerStart == TimerStart.LIMIT_REACHED
            ) {
                expiresAt = Math.addExact(now, policy.duration)
            }
        }

        resetType = policy.resetType
        signature = policy.signature
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
            "1",
            amount,
            expiresAt,
            resetType?.name?.lowercase().orEmpty(),
            Base64.getEncoder().encodeToString(signature.toByteArray(Charsets.UTF_8)),
            checkedAt,
            session,
            revision,
            0,
            limit
        ).joinToString("|")
    }

    companion object {
        fun decode(raw: String): EarningState {
            if (raw.isEmpty()) {
                return EarningState()
            }

            val parts = raw.split('|')
            require(parts.size == 10 && parts[0] == "1") { "Unsupported earning state" }

            return EarningState(
                amount = parts[1].toDouble(),
                expiresAt = parts[2].toLong(),
                resetType = parts[3].takeIf { it.isNotEmpty() }?.let { ResetType.valueOf(it.uppercase()) },
                signature = String(Base64.getDecoder().decode(parts[4]), Charsets.UTF_8),
                checkedAt = parts[5].toLong(),
                session = parts[6],
                revision = parts[7].toInt(),
                limit = parts[9].toDouble()
            ).also {
                require(it.amount.isFinite() && it.amount >= 0 && it.expiresAt >= 0 && it.checkedAt >= 0)
            }
        }
    }
}
