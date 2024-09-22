package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.simplify
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import kotlin.math.abs

object EffectMineRadiusOneDeep : MineBlockEffect<NoCompileData>("mine_radius_one_deep") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius to break!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")

        // Get the block face the player is interacting with
        val targetBlock = player.getTargetBlockExact(5) ?: return false
        val blockFace = block.getFace(targetBlock) ?: return false

        val blocks = mutableSetOf<Block>()

        // Determine the range and direction based on the block face
        val (xRange, yRange, zRange) = when (blockFace) {
            BlockFace.UP, BlockFace.DOWN -> {
                val yRange = if (blockFace == BlockFace.UP) 0..radius else -radius..0
                Triple(-radius..radius, yRange, -radius..radius)
            }
            BlockFace.NORTH, BlockFace.SOUTH -> {
                val zRange = if (blockFace == BlockFace.NORTH) -radius..0 else 0..radius
                Triple(-radius..radius, -radius..radius, zRange)
            }
            BlockFace.EAST, BlockFace.WEST -> {
                val xRange = if (blockFace == BlockFace.EAST) 0..radius else -radius..0
                Triple(xRange, -radius..radius, -radius..radius)
            }
            else -> return false  // In case of invalid or unexpected block face
        }

        for (x in xRange) {
            for (y in yRange) {
                for (z in zRange) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    if (config.getBool("no_corners")) {
                        val atXCorner = abs(x) == radius
                        val atYCorner = abs(y) == radius
                        val atZCorner = abs(z) == radius

                        if (atXCorner && atYCorner
                            || atXCorner && atZCorner
                            || atYCorner && atZCorner
                        ) {
                            continue
                        }
                    }

                    val toBreak = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

                    if (toBreak.location.blockY !in block.world.minHeight..block.world.maxHeight) {
                        continue
                    }

                    if (config.getStrings("blacklisted_blocks").containsIgnoreCase(toBreak.type.name)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.containsIgnoreCase(toBreak.type.name)) {
                            continue
                        }
                    }

                    if (config.getBoolOrNull("check_hardness") != false) {
                        if (toBreak.type.hardness > block.type.hardness) {
                            continue
                        }
                    }

                    if (toBreak.type.hardness < 0) {
                        continue
                    }

                    if (toBreak.type == Material.AIR) {
                        continue
                    }

                    if (!AntigriefManager.canBreakBlock(player, toBreak)) {
                        continue
                    }

                    blocks.add(toBreak)
                }
            }
        }

        player.breakBlocksSafely(blocks)

        return true
    }

}
