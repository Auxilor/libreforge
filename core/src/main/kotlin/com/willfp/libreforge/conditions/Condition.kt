package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.ViolationContext
import org.bukkit.entity.Player

abstract class Condition<T>(
    override val id: String
) : ConfigurableProperty() {
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
        compileData: T?
    ): Boolean

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, context: ViolationContext): T? {
        return null
    }
}
