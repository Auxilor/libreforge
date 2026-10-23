package com.willfp.libreforge.earning

import java.util.Base64

internal data class EarningState(
    var amount: Double = 0.0,
    var expiresAt: Long = 0,
    var kind: String = "",
    var signature: String = "",
    var checkedAt: Long = 0,
    var session: String = "",
    var revision: Int = -1,
    var limit: Double = 0.0
) {
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
        val changed = kind != policy.kind || signature != policy.signature

        if (kind.isEmpty()) {
            checkedAt = now
        } else if (kind != policy.kind) {
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

        kind = policy.kind
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
            kind,
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
                kind = parts[3],
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
