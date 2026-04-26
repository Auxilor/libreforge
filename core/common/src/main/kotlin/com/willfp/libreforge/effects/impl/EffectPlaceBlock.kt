package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectPlaceBlock : Effect<NoCompileData>("place_block") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("block", "You must specify the block to place!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val block = location.block
        val toPlace = Blocks.lookup(config.getString("block"))
        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        if (duration != null && duration > 0) {
            val oldBlock = Blocks.getBlock(block)
            toPlace.place(location)
            plugin.scheduler.runTaskLater(duration.toLong()) {
                oldBlock.place(location)
            }
        } else {
            toPlace.place(location)
        }

        return true
    }
}
