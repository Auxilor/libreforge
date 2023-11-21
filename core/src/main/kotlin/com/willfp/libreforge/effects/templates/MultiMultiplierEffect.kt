package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.core.map.nestedListMap
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.IdentifiedModifier
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
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

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        if (config.has(key)) {
            val elements = config.getStrings(key).mapNotNull { getElement(it) }

            for (element in elements) {
                modifiers[dispatcher.uuid][element] += IdentifiedModifier(identifiers.uuid) {
                    config.getDoubleFromExpression("multiplier", dispatcher.get())
                }
            }
        } else {
            globalModifiers[dispatcher.uuid] += IdentifiedModifier(identifiers.uuid) {
                config.getDoubleFromExpression("multiplier", dispatcher.get())
            }
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        globalModifiers[dispatcher.uuid].removeIf { it.uuid == identifiers.uuid }

        for (element in getAllElements()) {
            modifiers[dispatcher.uuid][element].removeIf { it.uuid == identifiers.uuid }
        }
    }

    protected fun getMultiplier(dispatcher: Dispatcher<*>, element: T): Double {
        var multiplier = 1.0

        for (modifier in globalModifiers[dispatcher.uuid]) {
            multiplier *= modifier.modifier
        }

        for (modifier in modifiers[dispatcher.uuid][element]) {
            multiplier *= modifier.modifier
        }

        return multiplier
    }

    @Deprecated(
        "Use getMultiplier(dispatcher: Dispatcher<*>, element: T) instead.",
        ReplaceWith("getMultiplier(player.toDispatcher(), element)"),
        DeprecationLevel.ERROR
    )
    protected fun getMultiplier(player: Player, element: T): Double =
        getMultiplier(player.toDispatcher(), element)

    /**
     * Get an element by [key], for example a stat.
     */
    abstract fun getElement(key: String): T?

    /**
     * Get all elements.
     */
    abstract fun getAllElements(): Collection<T>
}
