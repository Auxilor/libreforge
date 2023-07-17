package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player

object ConditionOnGround : Condition<NoCompileData>("on_ground") {
    override fun isMet(player: Player, config: Config, compileData: NoCompileData): Boolean {
        return !player.world.getBlockAt(player.location.clone().add(0.0, -1.0, 0.0)).isEmpty
    }

}