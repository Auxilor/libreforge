package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionInWater : Condition<NoCompileData>("in_water") {
    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return player.isInWater == (config.getBoolOrNull("in_water") ?: true) // Legacy support
    }
}
