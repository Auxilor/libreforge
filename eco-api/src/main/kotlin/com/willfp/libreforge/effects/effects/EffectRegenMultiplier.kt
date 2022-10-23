package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import com.willfp.libreforge.triggers.wrappers.WrappedRegenEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityRegainHealthEvent
import java.util.UUID

class EffectRegenMultiplier : Effect("regen_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        registeredModifiers.add(
            MultiplierModifier(
                uuid
            ) { config.getDoubleFromExpression("multiplier", player) }
        )
        modifiers[player.uniqueId] = registeredModifiers
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
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
            multiplier *= modifier.multiplier()
        }

        val wrapped = WrappedRegenEvent(event)
        wrapped.amount *= multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the regen multiplier!"
            )
        )

        return violations
    }
}
