package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.Registrable

abstract class TriggerGroup(
    val prefix: String
) : Registrable {
    abstract fun create(value: String): Trigger?

    override fun getID(): String {
        return prefix
    }
}
