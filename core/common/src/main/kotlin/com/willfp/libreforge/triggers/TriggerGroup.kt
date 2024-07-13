package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.Registrable
import com.willfp.libreforge.plugin

abstract class TriggerGroup(
    val prefix: String
) : Registrable {
    abstract fun create(value: String): Trigger?

    final override fun onRegister() {
        plugin.runWhenEnabled {
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }

    override fun getID(): String {
        return prefix
    }
}
