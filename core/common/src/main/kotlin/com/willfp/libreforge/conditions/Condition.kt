package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.HolderChange
import com.willfp.libreforge.HolderSignals
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.plugin
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
     * The change signals that can change this condition's result, or null for a condition that is
     * polled every condition interval.
     */
    open val invalidatedBy: Set<HolderChange>?
        get() = null

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

    final override fun onRegister() {
        plugin.runWhenEnabled {
            HolderSignals.registerCondition(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
