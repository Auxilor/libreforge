package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.effects.getEffectAmount
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedHungerEvent
import com.willfp.libreforge.triggers.wrappers.WrappedRegenEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import java.util.UUID
import kotlin.math.ceil

class EffectRegenMultiplier : Effect("regen_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(player: Player, config: Config) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        registeredModifiers.add(
            MultiplierModifier(
                uuid,
                config.getDouble("multiplier")
            )
        )
        modifiers[player.uniqueId] = registeredModifiers
    }

    override fun handleDisable(player: Player) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = this.getUUID(player.getEffectAmount(this))
        registeredModifiers.removeIf { it.uuid == uuid }
        modifiers[player.uniqueId] = registeredModifiers
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityRegainHealthEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }

        val player = event.entity

        if (player !is Player) {
            return
        }

        var multiplier = 1.0

        for (modifier in (modifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        val wrapped = WrappedRegenEvent(event)
        wrapped.amount *= multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the regen multiplier!"
                )
            )

        return violations
    }
}
