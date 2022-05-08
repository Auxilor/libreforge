package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Condition(
    id: String
) : ConfigurableProperty(id), Listener {
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
    open fun isConditionMet(
        player: Player,
        config: Config
    ): Boolean = true

    /**
     * Get if condition is met for a player.
     *
     * @param player The player.
     * @param config The config of the condition.
     * @return If met.
     */
    open fun isConditionMet(
        player: Player,
        config: Config,
        data: CompileData?
    ): Boolean = isConditionMet(player, config)

    open fun makeCompileData(config: Config, context: String): CompileData? {
        return null
    }
}

data class ConfiguredCondition internal constructor(
    val condition: Condition,
    val config: Config,
    val notMetLines: List<String>?,
    val compileData: CompileData? = null,
    val inverse: Boolean = false
) {
    fun isMet(player: Player): Boolean {
        val raw = condition.isConditionMet(player, config, compileData)

        return raw != inverse
    }
}
