package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
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
    abstract fun isMet(
        player: Player,
        config: Config,
        compileData: T
    ): Boolean

    override fun onRegister() {
        plugin.eventManager.registerListener(this)
    }
}
