package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.simplify
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.Vector

object EffectMineShape : MineBlockEffect<NoCompileData>("mine_shape") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("shape", "You must specify the shape to break!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val triggerBlock = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val shape = config.getStrings("shape")
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

        val direction = player.location.direction
        val forwardAxis = direction.clone().simplify()
        val worldUp = Vector(0.0, 1.0, 0.0)

        val upAxis: Vector
        val rightAxis: Vector
        if (forwardAxis.y != 0.0) {
            upAxis = Vector(direction.x, 0.0, direction.z).simplify()
            rightAxis = upAxis.clone().crossProduct(worldUp).simplify()
        } else {
            upAxis = worldUp.clone()
            rightAxis = forwardAxis.clone().crossProduct(worldUp).simplify()
        }

        val preventTriggers = config.getBool("prevent_trigger")
        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

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
                        if (targetBlock.type.hardness > triggerBlock.type.hardness) {
                            continue
                        }
                    }

                    if (targetBlock.type.hardness < 0) {
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

        player.breakBlocksSafely(blocksToBreak, preventTriggers)

        return true
    }
}
