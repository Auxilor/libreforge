package com.willfp.libreforge.earning

import com.willfp.eco.core.Eco
import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.libreforge.plugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

internal class EarningLimitScheduler {
    private data class Reset(val player: UUID?, val group: EarningLimitGroup)

    /** Scheduled reset timestamps in epoch milliseconds, per player or cron group. */
    private val scheduledResetTimes = ConcurrentHashMap<Reset, Long>()

    @Volatile
    private var task: EcoTask? = null

    fun start(groups: Collection<EarningLimitGroup>) {
        task?.cancel()
        task = plugin.scheduler.async().runTimer(1, 1) { tick() }
        reload(groups)
    }

    fun reload(groups: Collection<EarningLimitGroup>) {
        if (task?.isCancelled != false) return

        scheduledResetTimes.keys.removeIf { reset ->
            !reset.group.active || (reset.player == null) != (reset.group.policy.cron != null)
        }

        plugin.scheduler.async().run {
            if (task == null || groups.isEmpty()) return@run

            val saved = try {
                // The zero UUID belongs to the server profile, which stores cron metadata.
                val players = Eco.get().savedProfileUUIDs - UUID(0, 0)
                Eco.get().readAllProfileValuesForKeys(players, groups.map { it.key })
            } catch (exception: Exception) {
                plugin.logger.log(Level.SEVERE, "Failed to load earning limit states", exception)
                return@run
            }

            for (group in groups) {
                if (task == null) return@run
                if (!group.active) continue

                val players = group.players.toMutableSet()
                for ((player, raw) in saved[group.key].orEmpty()) {
                    val pending = try {
                        val state = EarningState.decode(raw as String)
                        state.amount > 0 || (group.policy.cron == null && state.expiresAt != 0L)
                    } catch (_: IllegalArgumentException) {
                        true
                    }
                    if (pending) players += player
                }

                for (player in players) {
                    if (task == null) return@run
                    refresh(player, group)
                }
            }
        }
    }

    fun schedule(player: UUID, group: EarningLimitGroup, policy: EarningPolicy, state: EarningState) {
        val pending = state.amount > 0 || (policy.cron == null && state.expiresAt != 0L)
        if (pending) group.players.add(player) else group.players.remove(player)

        val reset = Reset(if (policy.cron == null) player else null, group)
        scheduledResetTimes.compute(reset) { _, previous ->
            if (task == null || !group.active) return@compute previous
            if (group.policy !== policy) {
                plugin.scheduler.async().run { refresh(player, group) }
                return@compute previous
            }

            val expiresAt = state.expiresAt.takeIf { it != 0L && (policy.cron == null || group.players.isNotEmpty()) }
            if (policy.cron != null && expiresAt != null && previous != null) {
                minOf(previous, expiresAt)
            } else {
                expiresAt
            }
        }
    }

    private fun tick() {
        if (task == null) return

        val now = System.currentTimeMillis()
        for ((reset, expiresAt) in scheduledResetTimes) {
            if (task == null) return
            if (expiresAt > now || !scheduledResetTimes.remove(reset, expiresAt)) continue

            if (reset.player != null) {
                refresh(reset.player, reset.group)
            } else {
                for (member in reset.group.players) {
                    if (task == null) return
                    refresh(member, reset.group)
                }
            }
        }
    }

    private fun refresh(player: UUID, group: EarningLimitGroup) {
        try {
            EarningLimits.refresh(player, group)
        } catch (exception: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to reset earning limit ${group.key.key} for $player", exception)
        }
    }

    fun stop() {
        val current = task
        task = null
        current?.cancel()
        scheduledResetTimes.clear()
    }
}
