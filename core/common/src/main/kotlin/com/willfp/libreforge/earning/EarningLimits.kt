package com.willfp.libreforge.earning

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.plugin
import org.bukkit.OfflinePlayer
import java.time.ZoneId
import java.util.UUID

object EarningLimits {
    private data class Version(
        val policy: EarningPolicy,
        val since: Long
    )

    private data class Group(
        val key: PersistentDataKey<String>,
        val versions: List<Version>
    )

    private val session = UUID.randomUUID().toString()

    @Volatile
    private var groups = emptyMap<String, Group>()

    @Volatile
    private var active = emptySet<String>()

    internal val groupIds: Set<String>
        get() = active

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

        val next = groups.toMutableMap()

        for ((id, policy) in loaded) {
            val old = next[id]

            next[id] = if (old == null) {
                Group(
                    PersistentDataKey(
                        plugin.namespacedKeyFactory.create("earning_limit_$id"),
                        PersistentDataKeyType.STRING,
                        ""
                    ),
                    listOf(Version(policy, now))
                )
            } else if (old.versions.last().policy != policy) {
                old.copy(versions = old.versions + Version(policy, now))
            } else {
                old
            }
        }

        groups = next.toMap()
        active = loaded.keys.toSet()
    }

    fun validateGroup(config: Config, context: ViolationContext) {
        if (!config.has("earning-limit")) {
            return
        }

        val id = config.getString("earning-limit")

        if (id in active) {
            return
        }

        context.log(
            ConfigViolation(
                "earning-limit",
                "Unknown or invalid earning-limit group: $id"
            )
        )
    }

    private fun load(player: OfflinePlayer, group: Group, now: Long): EarningState {
        val state = EarningState.decode(player.profile.read(group.key))
        val versions = group.versions

        if (state.session != session) {
            val baseline = versions.indexOfLast { it.since <= state.checkedAt }.coerceAtLeast(0)

            state.advance(versions[baseline].policy, maxOf(versions[baseline].since, state.checkedAt), startup = true)
            state.session = session
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
        if (id !in active) {
            return null
        }

        val group = groups[id] ?: return null
        val state = load(player, group, System.currentTimeMillis())
        player.profile.write(group.key, state.encode())

        return state
    }

    internal fun reset(player: OfflinePlayer, id: String): Boolean {
        if (id !in active) {
            return false
        }

        val group = groups[id] ?: return false
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

    fun addEarned(player: OfflinePlayer, id: String?, amount: Double) {
        if (!amount.isFinite() || amount <= 0) {
            return
        }

        if (id == null || id !in active) {
            return
        }

        val group = groups[id] ?: return
        val now = System.currentTimeMillis()
        val state = load(player, group, now)

        state.amount += amount
        state.startTimer(group.versions.last().policy, now)
        player.profile.write(group.key, state.encode())
    }
}
