package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.KeyToMutableListMap
import com.willfp.libreforge.KeyToMutableListMapMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.MultiplierModifier
import org.bukkit.entity.Player
import java.util.UUID

abstract class MultiMultiplierEffect<T>(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val globalModifiers = KeyToMutableListMap<UUID, MultiplierModifier>()
    private val modifiers = KeyToMutableListMapMap<UUID, T, MultiplierModifier>()

    /**
     * The key to look for in arguments, e.g. "stat" or "skill".
     */
    abstract val key: String

    final override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        if (config.has(key)) {
            val element = getElement(config.getString(key)) ?: return

            modifiers[player.uniqueId][element] += object : MultiplierModifier(identifiers.uuid) {
                override fun getMultiplier() = config.getDoubleFromExpression("multiplier", player)
            }
        } else {
            globalModifiers[player.uniqueId] += object : MultiplierModifier(identifiers.uuid) {
                override fun getMultiplier() = config.getDoubleFromExpression("multiplier", player)
            }
        }
    }

    final override fun onDisable(player: Player, identifiers: Identifiers) {
        globalModifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }

        for (element in getAllElements()) {
            modifiers[player.uniqueId][element].removeIf { it.uuid == identifiers.uuid }
        }
    }

    protected fun getMultiplier(player: Player, element: T): Double {
        var multiplier = 1.0

        for (modifier in globalModifiers[player.uniqueId]) {
            multiplier *= modifier.multiplier
        }

        for (modifier in modifiers[player.uniqueId][element]) {
            multiplier *= modifier.multiplier
        }

        return multiplier
    }

    /**
     * Get an element by [key], for example a stat.
     */
    abstract fun getElement(key: String): T?

    /**
     * Get all elements.
     */
    abstract fun getAllElements(): Collection<T>
}
