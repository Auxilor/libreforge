package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Levelled
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object EffectReplaceNear : Effect<NoCompileData>("replace_near") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("radius_y", "You must specify the y radius!")
        require("replace_to", "You must specify the block to replace to!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false
        val world = block.world

        val radius = config.getIntFromExpression("radius", data)
        // todo: radiusY is not used, someone left it here
        // please use it or remove it, but don't leave it like this
        val radiusY = config.getIntFromExpression("radius_y", data)

        if (player.isSneaking && config.getBool("disable_on_sneak"))
            return false

        val replaceTo = Blocks.lookup(config.getString("replace_to"))

        val whitelist = config.getStringsOrNull("whitelist")
            ?.mapNotNull { Blocks.lookup(it) }?.toSet()

        val blacklist = config.getStringsOrNull("blacklist")
            ?.mapNotNull { Blocks.lookup(it) }?.toSet()

        val duration = config.getOrNull("duration") { getIntFromExpression(it, data) }

        val exposedBlocksOnly = config.getBool("exposed_only")
        val sourceBlocksOnly = config.getBool("source_only")

        for (y in (-radius..radius)) {
            val endY = block.y + y
            if (endY !in world.minHeight..world.maxHeight) {
                continue
            }

            for (x in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    val toReplace = world.getBlockAt(block.x + x, block.y + y, block.z + z)

                    if (blacklist != null) {
                        if (blacklist.any { it.matches(toReplace) }) {
                            continue
                        }
                    }

                    if (whitelist != null) {
                        if (!whitelist.any { it.matches(toReplace) }) {
                            continue
                        }
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

                    if (!(AntigriefManager.canBreakBlock(player, toReplace) && AntigriefManager.canPlaceBlock(
                            player,
                            toReplace
                        ))
                    ) {
                        continue
                    }

                    if (duration != null && duration > 0) {
                        val oldBlock = Blocks.getBlock(toReplace)
                        toReplace.setMetadata("rn-block", plugin.createMetadataValue(true))

                        plugin.scheduler.runTaskLater(duration.toLong()) {
                            if (toReplace.hasMetadata("rn-block")) {
                                oldBlock.place(toReplace.location)
                                toReplace.removeMetadata("rn-block", plugin)
                            }
                        }
                    }

                    replaceTo.place(toReplace.location)
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
