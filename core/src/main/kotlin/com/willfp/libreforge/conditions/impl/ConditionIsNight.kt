package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionIsNight : Condition<NoCompileData>("is_night") {
    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        val isNight = player.world.time in 12301..23849

        return isNight == (config.getBoolOrNull("is_night") ?: true) // Legacy
    }
}
