package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.core.map.nestedListMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.IdentifiedModifier
import org.bukkit.entity.Player
import java.util.UUID

abstract class MultiMultiplierEffect<T : Any>(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
    }

    private val globalModifiers = listMap<UUID, IdentifiedModifier>()
    private val modifiers = nestedListMap<UUID, T, IdentifiedModifier>()

    /**
     * The key to look for in arguments, e.g. "stat" or "skill".
     */
    abstract val key: String

    final override fun onEnable(player: Player, config: Config, identifiers: Identifiers, holder: ProvidedHolder, compileData: NoCompileData) {
        if (config.has(key)) {
            val elements = config.getStrings(key).mapNotNull { getElement(it) }

            for (element in elements) {
                modifiers[player.uniqueId][element] += IdentifiedModifier(identifiers.uuid) {
                    config.getDoubleFromExpression("multiplier", player)
                }
            }
        } else {
            globalModifiers[player.uniqueId] += IdentifiedModifier(identifiers.uuid) {
                config.getDoubleFromExpression("multiplier", player)
            }
        }
    }

    final override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        globalModifiers[player.uniqueId].removeIf { it.uuid == identifiers.uuid }

        for (element in getAllElements()) {
            modifiers[player.uniqueId][element].removeIf { it.uuid == identifiers.uuid }
        }
    }

    protected fun getMultiplier(player: Player, element: T): Double {
        var multiplier = 1.0

        for (modifier in globalModifiers[player.uniqueId]) {
            multiplier *= modifier.modifier
        }

        for (modifier in modifiers[player.uniqueId][element]) {
            multiplier *= modifier.modifier
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
