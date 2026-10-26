package com.willfp.libreforge.earning

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.profile
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.earning.EarningLimitGroup.Version
import org.bukkit.OfflinePlayer
import java.time.ZoneId

object EarningLimits {
    @Volatile
    private var groups = emptyMap<String, EarningLimitGroup>()

    internal val groupIds: Set<String>
        get() = groups.filterValues { it.active }.keys

    internal fun reload(config: Config, context: ViolationContext) {
        val now = System.currentTimeMillis()
        val loaded = linkedMapOf<String, EarningPolicy>()
        val sections = config.getSubsection("groups")

        for (id in sections.getKeys(false)) {
            try {
                require(Regex("[a-z0-9_-]+").matches(id)) { "group IDs must use lowercase letters, numbers, _ or -" }

                val section = sections.getSubsection(id)
                val limit = section.getDoubleOrNull("limit") ?: error("limit is required")
                val reset = section.getSubsection("reset")

                loaded[id] = when (reset.getString("type")) {
                    "cron" -> EarningPolicy(
                        limit = limit,
                        cron = reset.getStringOrNull("cron") ?: error("reset.cron is required"),
                        timezone = reset.getStringOrNull("timezone") ?: ZoneId.systemDefault().id
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

        val next = linkedMapOf<String, EarningLimitGroup>()

        for ((id, group) in groups) {
            if (id !in loaded) group.active = false
        }

        for ((id, policy) in loaded) {
            val group = groups[id] ?: EarningLimitGroup(id)

            if (group.versions.lastOrNull()?.policy != policy) {
                group.versions = group.versions + Version(policy, now)
            }

            group.active = true
            next[id] = group
        }

        groups = next.toMap()
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

    private fun load(player: OfflinePlayer, group: EarningLimitGroup, now: Long): EarningState {
        val state = EarningState.decode(player.profile.read(group.key))
        val versions = group.versions

        if (state.session != group.session) {
            val baseline = versions.indexOfLast { it.since <= state.checkedAt }.coerceAtLeast(0)

            state.advance(versions[baseline].policy, maxOf(versions[baseline].since, state.checkedAt), startup = true)
            state.session = group.session
            state.revision = baseline
        }

        for (revision in state.revision + 1 until versions.size) {
            val version = versions[revision]

            state.advance(versions[revision - 1].policy, version.since)
            state.advance(version.policy, version.since, transition = true)
            state.revision = revision
        }

        state.advance(versions.last().policy, now)

        return state
    }

    internal fun getState(player: OfflinePlayer, id: String): EarningState? {
        return groups[id]?.let { getState(player, it) }
    }

    private fun getState(player: OfflinePlayer, group: EarningLimitGroup): EarningState? {
        if (!group.active) {
            return null
        }

        val state = load(player, group, System.currentTimeMillis())
        player.profile.write(group.key, state.encode())

        return state
    }

    internal fun reset(player: OfflinePlayer, id: String): Boolean {
        val group = groups[id] ?: return false

        if (!group.active) {
            return false
        }

        val now = System.currentTimeMillis()
        val state = load(player, group, now)

        state.reset()
        state.advance(group.versions.last().policy, now)
        player.profile.write(group.key, state.encode())

        return true
    }

    fun getRemaining(player: OfflinePlayer, id: String): Double {
        return getState(player, id)?.remaining ?: 0.0
    }

    internal fun getRemaining(player: OfflinePlayer, group: EarningLimitGroup): Double {
        return getState(player, group)?.remaining ?: 0.0
    }

    fun addEarned(player: OfflinePlayer, group: EarningLimitGroup?, amount: Double) {
        if (group == null || !group.active || !amount.isFinite() || amount <= 0) {
            return
        }

        val now = System.currentTimeMillis()
        val state = load(player, group, now)

        state.amount += amount
        state.startTimer(group.versions.last().policy, now)
        player.profile.write(group.key, state.encode())
    }
}
