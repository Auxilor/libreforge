package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionInAir : Condition<NoCompileData>("in_air") {
    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return player.location.block.isEmpty == (config.getBoolOrNull("in_air") ?: true) // Legacy support
    }
}
