package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import org.bukkit.entity.Player
import java.util.UUID

abstract class GenericMultiplierEffect(id: String) : Effect(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val modifiers = mutableMapOf<UUID, MutableList<MultiplierModifier>>()

    final override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        registeredModifiers.add(
            MultiplierModifier(uuid) {
                config.getDoubleFromExpression("multiplier", player)
            }
        )
        modifiers[player.uniqueId] = registeredModifiers
    }

    final override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val registeredModifiers = modifiers[player.uniqueId] ?: mutableListOf()
        val uuid = identifiers.uuid
        registeredModifiers.removeIf { it.uuid == uuid }
        modifiers[player.uniqueId] = registeredModifiers
    }

    fun getMultiplier(player: Player): Double {
        var multiplier = 1.0

        for (modifier in (modifiers[player.uniqueId] ?: emptyList())) {
            multiplier *= modifier.multiplier
        }

        return multiplier
    }
}
