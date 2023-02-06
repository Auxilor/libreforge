package com.willfp.libreforge.integrations.paper

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import java.util.UUID

class EffectElytraBoostSaveChance : Effect("elytra_boost_save_chance") {
    override val arguments = arguments {
        require("chance", "You must specify the chance to not consume rockets!")
    }

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
            MultiplierModifier(uuid) {
                config.getDoubleFromExpression("chance", player)
            }
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
    fun handle(event: PlayerElytraBoostEvent) {
        val player = event.player

        var chance = 100.0

        for (modifier in (modifiers[player.uniqueId] ?: emptyList())) {
            chance *= (100 - modifier.multiplier)
        }

        if (NumberUtils.randFloat(0.0, 100.0) > chance) {
            event.setShouldConsume(false)
        }
    }
}
