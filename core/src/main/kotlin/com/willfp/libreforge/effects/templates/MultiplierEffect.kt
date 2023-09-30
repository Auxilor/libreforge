package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.IdentifiedModifier
import org.bukkit.entity.Player
import java.util.UUID

abstract class MultiplierEffect(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val modifiers = listMap<UUID, IdentifiedModifier>()

    final override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        modifiers[player.uniqueId] += IdentifiedModifier(identifiers.uuid) {
            config.getDoubleFromExpression("multiplier", player)
        }
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }
    }

    protected fun getMultiplier(player: Player): Double {
        var multiplier = 1.0

        for (modifier in modifiers[player.uniqueId]) {
            multiplier *= modifier.modifier
        }

        return multiplier
    }
}
