package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.UUID

class EffectSellMultiplier : Effect("sell_multiplier") {
    private val modifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    @EventHandler
    fun handle(event: ShopSellEvent) {
        event.price *= getMultiplier(event.player)
    }

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

    private fun getMultiplier(player: Player): Double {
        var multiplier = 1.0

        for (modifier in (modifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier()
        }

        return multiplier
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("multiplier")) violations.add(
            ConfigViolation(
                "multiplier",
                "You must specify the sell multiplier!"
            )
        )

        return violations
    }
}
