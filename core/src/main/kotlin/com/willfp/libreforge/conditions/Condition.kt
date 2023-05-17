package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.plugin
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Condition<T>(
    override val id: String
) : Compilable<T>(), Listener {
    /**
     * Get if the condition is met.
     *
     * @param player The player.
     * @param config The config.
     * @param compileData The compile data.
     * @return If met.
     */
    open fun isMet(
        player: Player,
        config: Config,
        compileData: T
    ): Boolean {
        return true
    }

    /**
     * Get if the condition is met.
     *
     * @param player The player.
     * @param config The config.
     * @param holder The provided holder.
     * @param compileData The compile data.
     * @return If met.
     */
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
            plugin.eventManager.unregisterListener(this)
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }
}
