package com.willfp.libreforge.api.conditions

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.LibReforge
import com.willfp.libreforge.api.Watcher
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.Objects

abstract class Condition(
    val id: String
) : Listener {
    protected val plugin = LibReforge.instance.plugin

    init {
        postInit()
    }

    private fun postInit() {
        Conditions.addNewCondition(this)
    }

    /**
     * Get if condition is met for a player.
     *
     * @param player The player.
     * @param config The config of the condition.
     * @return If met.
     */
    abstract fun isConditionMet(
        player: Player,
        config: JSONConfig
    ): Boolean

    override fun equals(other: Any?): Boolean {
        if (other !is Condition) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(this.id)
    }
}