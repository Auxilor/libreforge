package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Levelled
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object EffectReplaceNear : Effect<NoCompileData>("replace_near") {
    override val description = "Replaces blocks of one type with another within a specified radius around the trigger location."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius!",
            description = "The horizontal radius to search for blocks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "3"
        )
        require(
            "radius_y",
            "You must specify the y radius!",
            description = "The vertical radius to search for blocks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "2"
        )
        require(
            "replace_to",
            "You must specify the block to replace to!",
            description = "The block type to replace matching blocks with.",
            type = ArgType.BLOCK
        )
        optional(
            "whitelist",
            description = "A list of block types that are allowed to be replaced. If omitted, all non-air blocks are eligible.",
            type = ArgType.BLOCK_LIST
        )
        optional(
            "blacklist",
            description = "A list of block types that should never be replaced.",
            type = ArgType.BLOCK_LIST
        )
        optional(
            "duration",
            description = "How long (in ticks) before the replaced blocks revert to their original type. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "200"
        )
        optional(
            "disable_on_sneak",
            description = "Whether to skip replacement when the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "exposed_only",
            description = "Whether to only replace blocks that have air directly above them.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "source_only",
            description = "Whether to only replace source liquid blocks (level 0).",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)
        val radiusY = config.getIntFromExpression("radius_y", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val replaceTo = Items.lookup(config.getString("replace_to")).item.type

        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklist").map { Blocks.lookup(it) }

        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        val exposedBlocksOnly = config.getBool("exposed_only")
        val sourceBlocksOnly = config.getBool("source_only")

        for (x in (-radius..radius)) {
            for (y in (-radiusY..radiusY)) {
                for (z in (-radius..radius)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    val toReplace = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

                    if (blacklist.matches(toReplace)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.matches(toReplace)) {
                            continue
                        }
                    }

                    if (toReplace.type == Material.AIR && whitelist?.matches(toReplace) != true) {
                        continue
                    }

                    if (exposedBlocksOnly && !toReplace.getRelative(BlockFace.UP, 1).isEmpty) {
                        continue
                    }

                    if (sourceBlocksOnly && toReplace.isLiquid) {
                        val liquidData = toReplace.blockData
                        if (liquidData is Levelled) {
                            if (liquidData.level != 0) {
                                continue
                            }
                        }
                    }

                    if (!(AntigriefManager.canBreakBlock(player, toReplace) && AntigriefManager.canPlaceBlock(player, toReplace))) {
                        continue
                    }

                    if (duration != null && duration > 0) {
                        val oldBlock = toReplace.type
                        val oldBlockData = toReplace.blockData
                        toReplace.setMetadata("rn-block", plugin.createMetadataValue(true))

                        plugin.scheduler.runLater(duration.toLong()) {
                            if (toReplace.hasMetadata("rn-block")) {
                                toReplace.type = oldBlock
                                toReplace.blockData = oldBlockData
                                toReplace.removeMetadata("rn-block", plugin)
                            }
                        }
                    }

                    toReplace.type = replaceTo
                }
            }
        }

        return true
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        if (!block.hasMetadata("rn-block")) {
            return
        }

        block.removeMetadata("rn-block", plugin)
        block.type = Material.AIR
        event.isCancelled = true
    }
}
