package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block

object EffectMineShape : MineBlockEffect<NoCompileData>("mine_shape") {
    override val description = "Breaks blocks in a custom 2D grid shape relative to the block the player mines."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "shape",
            "You must specify the shape to break!",
            description = "A list of strings forming a grid where 'T' is the trigger block and 'X' marks blocks to break.",
            type = ArgType.STRING_LIST,
            example = listOf("XXX", "XTX", "XXX")
        )
        optional(
            "depth",
            description = "How many layers deep to mine behind the trigger block. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1"
        )
        optional(
            "whitelist",
            description = "A list of blocks that are allowed to be broken. Defaults to all blocks.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "blacklisted_blocks",
            description = "A list of blocks that will never be broken by this effect.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "prevent_trigger",
            description = "Whether the broken blocks should fire further libreforge triggers. " +
                    "true breaks them silently, firing no events at all; keep_events breaks them normally, " +
                    "so drops and block events still happen, but stops the mine_block trigger from running again.",
            type = ArgType.STRING,
            default = "false",
            choices = listOf("false", "true", "keep_events")
        )
        optional(
            "disable_on_sneak",
            description = "Whether to disable the shape mining when the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "check_hardness",
            description = "Whether to skip blocks harder than the trigger block. Defaults to true.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "use_blockface",
            description = "Whether to orient the shape by the face of the block the player is " +
                    "looking at, rather than by their look direction. Falls back to the look " +
                    "direction if the player isn't looking at the trigger block.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val triggerBlock = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val shape = config.getFormattedStrings("shape", data)
        if (shape.isEmpty()) {
            return false
        }

        var triggerRow = -1
        var triggerColumn = -1
        run {
            for ((row, line) in shape.withIndex()) {
                for ((column, character) in line.withIndex()) {
                    if (character == 'T' || character == 't') {
                        triggerRow = row
                        triggerColumn = column
                        return@run
                    }
                }
            }
        }

        if (triggerRow == -1) {
            return false
        }

        val axes = resolveMiningAxes(config, player, triggerBlock)
        val forwardAxis = axes.forward
        val upAxis = axes.up
        val rightAxis = axes.right

        val preventTriggers = preventTriggerMode(config)
        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getFormattedStrings("blacklisted_blocks", data).map { Blocks.lookup(it) }

        val depthLayers = (if (config.has("depth")) config.getIntFromExpression("depth", data) else 1)
            .coerceAtLeast(1)

        val blocksToBreak = mutableSetOf<Block>()

        for (layer in 0 until depthLayers) {
            val depthOffset = layer.toDouble()

            for ((row, line) in shape.withIndex()) {
                for ((column, character) in line.withIndex()) {
                    val isTriggerBlock = row == triggerRow && column == triggerColumn
                    val shouldMine = character == 'X' || character == 'x' || (isTriggerBlock && layer > 0)
                    if (!shouldMine) {
                        continue
                    }

                    val rightOffset = (column - triggerColumn).toDouble()
                    val upOffset = (triggerRow - row).toDouble()

                    val targetBlock = triggerBlock.world.getBlockAt(
                        triggerBlock.location.clone().add(
                            rightAxis.x * rightOffset + upAxis.x * upOffset + forwardAxis.x * depthOffset,
                            rightAxis.y * rightOffset + upAxis.y * upOffset + forwardAxis.y * depthOffset,
                            rightAxis.z * rightOffset + upAxis.z * upOffset + forwardAxis.z * depthOffset
                        )
                    )

                    if (targetBlock.location.blockY !in triggerBlock.world.minHeight..triggerBlock.world.maxHeight) {
                        continue
                    }

                    if (blacklist.matches(targetBlock)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.matches(targetBlock)) {
                            continue
                        }
                    }

                    if (config.getBoolOrNull("check_hardness") != false) {
                        if (Blocks.hardness(targetBlock) > Blocks.hardness(triggerBlock)) {
                            continue
                        }
                    }

                    if (Blocks.hardness(targetBlock) < 0) {
                        continue
                    }

                    if (targetBlock.type == Material.AIR) {
                        continue
                    }

                    if (!AntigriefManager.canBreakBlock(player, targetBlock)) {
                        continue
                    }

                    blocksToBreak.add(targetBlock)
                }
            }
        }

        data.breakBlocksSafely(blocksToBreak, preventTriggers)

        return true
    }
}
