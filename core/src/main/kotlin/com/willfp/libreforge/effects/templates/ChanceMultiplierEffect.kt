package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.randDouble
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import java.util.UUID

abstract class ChanceMultiplierEffect(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("chance", "You must specify the chance!")
    }

    private val modifiers = listMap<UUID, MultiplierModifier>()

    final override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        modifiers[player.uniqueId] += MultiplierModifier(identifiers.uuid) {
            config.getDoubleFromExpression("multiplier", player)
        }
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }
    }

    protected fun passesChance(player: Player): Boolean {
        var chance = 1.0

        for (modifier in modifiers[player.uniqueId]) {
            chance *= (100 - modifier.multiplier) / 100
        }

        return randDouble(0.0, 1.0) > chance
    }
}
