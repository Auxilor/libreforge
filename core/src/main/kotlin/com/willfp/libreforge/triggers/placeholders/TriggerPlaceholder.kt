package com.willfp.libreforge.triggers.placeholders

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.event.Listener

abstract class TriggerPlaceholder(
    override val id: String
) : Listener, KRegistrable {
    /**
     * Create placeholders for a [trigger].
     */
    abstract fun createPlaceholders(trigger: DispatchedTrigger): Collection<NamedValue>

    final override fun onRegister() {
        plugin.runWhenEnabled {
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
