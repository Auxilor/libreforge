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
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import java.util.LinkedList

object EffectFloodFill : Effect<NoCompileData>("flood_fill") {
    private val faces = arrayOf(
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST
    )

    override val parameters = setOf(
        TriggerParameter.BLOCK
    )

    override val arguments = arguments {
        require("replace_to", "You must specify the block to replace to!")
        require("max_blocks", "You must specify the maximum blocks to replace!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val origin = data.block ?: return false
        val targetType = origin.type
        val maxBlocks = config.getIntFromExpression("max_blocks", data)
        val replaceTo = Blocks.lookup(config.getString("replace_to"))
        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        val visited = mutableSetOf<Block>()
        val toReplace = mutableListOf<Block>()
        val queue = LinkedList<Block>()
        queue.add(origin)
        visited.add(origin)

        while (queue.isNotEmpty() && toReplace.size < maxBlocks) {
            val current = queue.poll()
            if (current.type != targetType) continue
            toReplace.add(current)
            for (face in faces) {
                val neighbor = current.getRelative(face)
                if (neighbor !in visited && neighbor.type == targetType) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }

        for (block in toReplace) {
            if (duration != null && duration > 0) {
                val oldBlock = Blocks.getBlock(block)
                replaceTo.place(block.location)
                plugin.scheduler.runTaskLater(duration.toLong()) {
                    oldBlock.place(block.location)
                }
            } else {
                replaceTo.place(block.location)
            }
        }

        return true
    }
}
