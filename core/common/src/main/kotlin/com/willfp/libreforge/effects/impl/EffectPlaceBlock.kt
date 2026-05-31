package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectPlaceBlock : Effect<NoCompileData>("place_block") {
    override val description = "Places a block at the trigger location, optionally reverting it after a duration."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "block",
            "You must specify the block to place!",
            description = "The block to place at the trigger location.",
            type = ArgType.BLOCK
        )
        optional(
            "duration",
            description = "How many ticks before the block reverts to its original state. Omit to place permanently. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val block = location.block
        val toPlace = Blocks.lookup(config.getString("block"))
        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        if (duration != null && duration > 0) {
            val oldBlock = Blocks.getBlock(block)
            toPlace.place(location)
            val placedType = location.block.type
            plugin.scheduler.runLater(duration.toLong()) {
                if (location.block.type == placedType) {
                    oldBlock.place(location)
                }
            }
        } else {
            toPlace.place(location)
        }

        return true
    }
}
