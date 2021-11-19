package com.willfp.libreforge.api.triggers

import com.willfp.libreforge.api.LibReforge
import org.bukkit.event.Listener
import java.util.Objects

abstract class Trigger(
    val id: String
) : Listener {
    protected val plugin = LibReforge.plugin

    init {
        postInit()
    }

    private fun postInit() {
        Triggers.addNewTrigger(this)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Trigger) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}