package com.willfp.libreforge.earning

import com.willfp.eco.core.Eco
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.PlayerProfile
import com.willfp.eco.core.data.ServerProfile
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.plugin
import org.bukkit.OfflinePlayer
import java.util.UUID
import java.util.logging.Level

object EarningLimits {
    private val scheduler = EarningLimitScheduler()
    private val startedAt = System.currentTimeMillis()
    private val idPattern = Regex("[a-z0-9_-]+")

    @Volatile
    private var groups = emptyMap<String, EarningLimitGroup>()

    internal val groupIds: Set<String>
        get() = groups.filterValues { it.active }.keys

    internal fun reload(config: Config, context: ViolationContext) {
        val loaded = linkedMapOf<String, EarningPolicy>()
        val sections = config.getSubsection("groups")

        for (id in sections.getKeys(false)) {
            try {
                require(idPattern.matches(id)) { "group IDs must use lowercase letters, numbers, _ or -" }

                val section = sections.getSubsection(id)
                val limit = section.getDoubleOrNull("limit") ?: error("limit is required")
                val reset = section.getSubsection("reset")

                loaded[id] = when (reset.getString("type")) {
                    "cron" -> EarningPolicy(
                        limit = limit,
                        cron = reset.getStringOrNull("cron") ?: error("reset.cron is required")
                    )
                    "interval" -> EarningPolicy(
                        limit = limit,
                        duration = parseEarningDuration(reset.getString("duration")),
                        timerStart = when (reset.getStringOrNull("timer-start") ?: "first-earning") {
                            "first-earning" -> TimerStart.FIRST_EARNING
                            "limit-reached" -> TimerStart.LIMIT_REACHED
                            else -> error("timer-start must be first-earning or limit-reached")
                        }
                    )
                    else -> error("reset.type must be cron or interval")
                }
            } catch (exception: Exception) {
                context.with("groups.$id").log(
                    ConfigViolation(
                        "group",
                        exception.message ?: "Invalid earning limit"
                    )
                )
            }
        }

        val now = System.currentTimeMillis()
        val profile = ServerProfile.load()
        val next = linkedMapOf<String, EarningLimitGroup>()

        for ((id, group) in groups) {
            if (id !in loaded) {
                group.active = false
                if (group.policy.cron != null) {
                    profile.write(group.key, "")
                }
            }
        }

        for ((id, policy) in loaded) {
            val group = groups[id] ?: EarningLimitGroup(id)
            val saved = profile.read(group.key)
            val signature = policy.cronSignature

            val cronChangedAt = if (signature != null) {
                val parts = saved.split('|', limit = 2)
                if (parts.size == 2 && parts[1] == signature) {
                    parts[0].toLongOrNull() ?: now
                } else {
                    now
                }
            } else {
                0
            }

            val updated = if (signature == null) "" else "$cronChangedAt|$signature"
            if (saved != updated) {
                profile.write(group.key, updated)
            }

            group.policy = policy.copy(cronChangedAt = cronChangedAt)
            group.active = true
            next[id] = group
        }

        groups = next.toMap()
        scheduler.reload(groups.values)
    }

    internal fun start() {
        scheduler.start(groups.values)
    }

    internal fun stop() {
        scheduler.stop()
    }

    fun validateGroup(config: Config, context: ViolationContext): EarningLimitGroup? {
        val id = config.getStringOrNull("earning-limit") ?: return null
        val group = groups[id]

        if (group?.active == true) {
            return group
        }

        context.log(
            ConfigViolation(
                "earning-limit",
                "Unknown or invalid earning-limit group: $id"
            )
        )

        return group ?: EarningLimitGroup(id)
    }

    private fun update(
        player: UUID,
        group: EarningLimitGroup,
        action: (EarningState, EarningPolicy, Long) -> Unit = { _, _, _ -> }
    ) {
        PlayerProfile.load(player).compute(group.key) { raw ->
            val now = System.currentTimeMillis()
            val policy = group.policy
            val state = try {
                EarningState.decode(raw)
            } catch (exception: IllegalArgumentException) {
                plugin.logger.log(Level.WARNING, "Resetting invalid earning limit ${group.key.key} for $player", exception)
                EarningState()
            }
            resetIfExpiredBeforeStartup(state)
            state.applyPolicy(policy, now)
            action(state, policy, now)

            scheduler.schedule(player, group, policy, state)
            state.encode()
        }
    }

    private fun resetIfExpiredBeforeStartup(state: EarningState) {
        if (state.checkedAt < startedAt && state.expiresAt != 0L && state.expiresAt <= startedAt) {
            state.reset()
        }
    }

    internal fun refresh(player: UUID, group: EarningLimitGroup) {
        if (group.active) update(player, group)
    }

    internal fun getState(player: OfflinePlayer, id: String): EarningState? {
        return groups[id]?.let { getState(player, it) }
    }

    private fun getState(player: OfflinePlayer, group: EarningLimitGroup): EarningState? {
        if (!group.active) {
            return null
        }

        val uuid = Eco.get().playerProfileResolver.resolve(player)
        val state = try {
            EarningState.decode(PlayerProfile.load(uuid).read(group.key))
        } catch (_: IllegalArgumentException) {
            update(uuid, group)
            return getState(player, group)
        }

        resetIfExpiredBeforeStartup(state)
        state.applyPolicy(group.policy, System.currentTimeMillis())
        return state
    }

    internal fun reset(player: OfflinePlayer, id: String): Boolean {
        val group = groups[id] ?: return false

        if (!group.active) {
            return false
        }

        update(Eco.get().playerProfileResolver.resolve(player), group) { state, policy, now ->
            state.reset()
            state.applyPolicy(policy, now)
        }

        return true
    }

    /**
     * Applies the earning limit and records earnings.
     * This does not pay the reward, it needs to be done separately.
     *
     * @return Amount to actually be paid
     */
    fun addEarned(
        player: OfflinePlayer,
        group: EarningLimitGroup,
        amount: Double,
        convert: (Double) -> Double = { it }
    ): Double {
        if (!group.active) {
            plugin.logger.warning("Unknown or invalid earning-limit group: ${group.id}")
            return 0.0
        }

        if (amount == 0.0) return 0.0

        var earned = 0.0
        update(Eco.get().playerProfileResolver.resolve(player), group) { state, policy, now ->
            val allowed = minOf(amount, state.remaining)
            earned = if (allowed == 0.0) 0.0 else convert(allowed)

            if (earned.isFinite() && earned > 0) {
                state.amount += earned
                state.startTimer(policy, now)
            }
        }

        return earned
    }
}
