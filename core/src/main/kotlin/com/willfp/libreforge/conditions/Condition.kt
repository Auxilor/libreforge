package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Dispatcher
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Condition<T>(
    override val id: String
) : Compilable<T>(), Listener {
    /**
     * Whether this condition is enabled.
     */
    open var isEnabled: Boolean = false
        protected set

    /**
     * Enable the condition.
     */
    fun enable() {
        // Only register if not enabled before
        if (!isEnabled) {
            plugin.runWhenEnabled {
                plugin.eventManager.unregisterListener(this)
                plugin.eventManager.registerListener(this)
            }
        }

        isEnabled = true
    }

    /**
     * Get if the condition is met for a [dispatcher], with a [config], [holder], and [compileData].
     */
    open fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: T
    ): Boolean {
        return true
    }

    /**
     * Get if the condition is met for a [player], with a [config], and [compileData].
     */
    @Deprecated(
        "Use isMet(dispatcher, config, holder, compileData) instead.",
        ReplaceWith("true")
    )
    open fun isMet(
        player: Player,
        config: Config,
        compileData: T
    ): Boolean {
        return true
    }

    /**
     * Get if the condition is met for a [player], with a [config], [holder], and [compileData].
     */
    @Deprecated(
        "Use isMet(dispatcher, config, holder, compileData) instead.",
        ReplaceWith("true")
    )
    open fun isMet(
        player: Player,
        config: Config,
        holder: ProvidedHolder,
        compileData: T
    ): Boolean {
        return true
    }

    final override fun onRegister() {
        plugin.runWhenEnabled {
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
