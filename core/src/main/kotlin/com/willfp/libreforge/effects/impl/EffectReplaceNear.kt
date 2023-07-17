package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Levelled
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

object EffectReplaceNear : MineBlockEffect<NoCompileData>("replace_near") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius_x", "You must specify the radius to replace!")
        require("radius_y", "You must specify the radius to replace!")
        require("radius_z", "You must specify the radius to replace!")
        require("replace_to", "You must specify the block to replace to!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radiusX = config.getIntFromExpression("radius_x", data)
        val radiusY = config.getIntFromExpression("radius_y", data)
        val radiusZ = config.getIntFromExpression("radius_z", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val replaceTo = Material.getMaterial(config.getString("replace_to").uppercase()) ?: return false

        val whitelist = config.getStringsOrNull("whitelist")

        val duration = config.getDoubleFromExpression("duration")

        val exposed = config.getBool("exposed_only")

        val flowing = config.getBool("source_only")

        for (x in (-radiusX..radiusX)) {
            for (y in (-radiusY..radiusY)) {
                for (z in (-radiusZ..radiusZ)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    val toReplace = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

                    if (config.getStrings("blacklisted_blocks").containsIgnoreCase(toReplace.type.name)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.containsIgnoreCase(toReplace.type.name)) {
                            continue
                        }
                    }

                    if (toReplace.type == Material.AIR) {
                        continue
                    }

                    if (exposed && !toReplace.getRelative(BlockFace.UP, 1).isEmpty) {
                        continue
                    }

                    if (flowing && toReplace.isLiquid) {
                        val liquidData = toReplace.blockData
                        if (liquidData is Levelled) {
                            if (liquidData.level != 0) {
                                continue
                            }
                        }
                    }

                    if (!AntigriefManager.canBreakBlock(player, toReplace)) {
                        continue
                    }

                    if (duration > 0) {
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
