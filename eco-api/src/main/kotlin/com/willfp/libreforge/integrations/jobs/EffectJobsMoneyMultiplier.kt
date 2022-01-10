package com.willfp.libreforge.integrations.jobs

import com.gamingmesh.jobs.api.JobsPaymentEvent
import com.gamingmesh.jobs.container.CurrencyType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.effects.getEffectAmount
import com.willfp.libreforge.getDouble
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.*

class EffectJobsMoneyMultiplier : Effect("jobs_money_multiplier") {
    private val globalModifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(player: Player, config: Config) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        registeredModifiers.add(
            MultiplierModifier(
                uuid,
                config.getDouble("multiplier", player)
            )
        )
        globalModifiers[player.uniqueId] = registeredModifiers
    }

    override fun handleDisable(player: Player) {
        val registeredModifiers = globalModifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        globalModifiers[player.uniqueId] = registeredModifiers
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: JobsPaymentEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.player

        var multiplier = 1.0

        for (modifier in (globalModifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        var money = event.payment[CurrencyType.MONEY] ?: return
        money *= multiplier
        event.payment[CurrencyType.MONEY] = money
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the money multiplier!"
            )
        )

        return violations
    }
}
