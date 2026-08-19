package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorBlockToRelative : Mutator<NoCompileData>("block_to_relative") {
    override val description = "Sets the block to another block offset from it."

    override val categories = setOf("block")

    override val arguments = arguments {
        optional(
            "add_x",
            description = "The number of blocks to offset along the X axis.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "1"
        )
        optional(
            "add_y",
            description = "The number of blocks to offset along the Y axis.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "-1"
        )
        optional(
            "add_z",
            description = "The number of blocks to offset along the Z axis.",
            type = ArgType.EXPRESSION,
            default = "0",
            example = "0"
        )
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val block = data.block ?: return data

        val addX = if (config.has("add_x")) config.getIntFromExpression("add_x", data) else 0
        val addY = if (config.has("add_y")) config.getIntFromExpression("add_y", data) else 0
        val addZ = if (config.has("add_z")) config.getIntFromExpression("add_z", data) else 0

        return data.copy(
            block = block.getRelative(addX, addY, addZ)
        )
    }
}
