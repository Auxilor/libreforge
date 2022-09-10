package com.willfp.libreforge.integrations.ecojobs

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.ecojobs.api.event.PlayerJobExpGainEvent
import com.willfp.ecojobs.jobs.Job
import com.willfp.ecojobs.jobs.Jobs
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectJobXpMultiplier : Effect("job_xp_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableMap<Job, MutableList<MultiplierModifier>>>()
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        if (config.has("jobs")) {
            val jobs = config.getStrings("jobs").mapNotNull { Jobs.getByID(it) }
            for (job in jobs) {
                val jobModifiers = modifiers[player.uniqueId] ?: mutableMapOf()
                val registeredModifiers = jobModifiers[job] ?: mutableListOf()
                val uuid = identifiers.uuid
                registeredModifiers.removeIf { it.uuid == uuid }
                registeredModifiers.add(
                    MultiplierModifier(
                        uuid,
                        config.getDoubleFromExpression("multiplier", player)
                    )
                )
                jobModifiers[job] = registeredModifiers
                modifiers[player.uniqueId] = jobModifiers
            }
        } else {
            val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
            val uuid = identifiers.uuid
            registeredModifiers.removeIf { it.uuid == uuid }
            registeredModifiers.add(
                MultiplierModifier(
                    uuid,
                    config.getDoubleFromExpression("multiplier", player)
                )
            )
            globalModifiers[player.uniqueId] = registeredModifiers
        }


    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers


        for (job in Jobs.values()) {
            val jobModifierMap = modifiers[player.uniqueId] ?: mutableMapOf()
            val registeredJobModifiers = jobModifierMap[job] ?: mutableListOf()
            registeredJobModifiers.removeIf { it.uuid == uuid }
            jobModifierMap[job] = registeredJobModifiers
            modifiers[player.uniqueId] = jobModifierMap
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerJobExpGainEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }


        for (modifier in (modifiers[player.uniqueId]?.get(event.job) ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        val wrapped = WrappedJobXpEvent(event)
        wrapped.amount = wrapped.amount * multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the xp multiplier!"
            )
        )

        return violations
    }
}
