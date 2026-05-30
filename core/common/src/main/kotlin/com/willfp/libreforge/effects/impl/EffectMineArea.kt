package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.simplify
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.Vector

object EffectMineArea : MineBlockEffect<NoCompileData>("mine_area") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("area", "You must specify the area pattern to break!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val area = config.getStrings("area")
        if (area.isEmpty()) {
            return false
        }

        var tRow = -1
        var tCol = -1
        run {
            for ((r, line) in area.withIndex()) {
                for ((c, ch) in line.withIndex()) {
                    if (ch == 'T' || ch == 't') {
                        tRow = r
                        tCol = c
                        return@run
                    }
                }
            }
        }

        if (tRow == -1) {
            return false
        }

        val dir = player.location.direction
        val depth = dir.clone().simplify()
        val worldUp = Vector(0.0, 1.0, 0.0)

        val up: Vector
        val right: Vector
        if (depth.y != 0.0) {
            up = Vector(dir.x, 0.0, dir.z).simplify()
            right = up.clone().crossProduct(worldUp).simplify()
        } else {
            up = worldUp.clone()
            right = depth.clone().crossProduct(worldUp).simplify()
        }

        val preventTriggers = config.getBool("prevent_trigger")
        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

        val blocks = mutableSetOf<Block>()

        for ((r, line) in area.withIndex()) {
            for ((c, ch) in line.withIndex()) {
                if (ch != 'X' && ch != 'x') {
                    continue
                }

                val dx = (c - tCol).toDouble()
                val dy = (tRow - r).toDouble()

                val toBreak = block.world.getBlockAt(
                    block.location.clone().add(
                        right.x * dx + up.x * dy,
                        right.y * dx + up.y * dy,
                        right.z * dx + up.z * dy
                    )
                )

                if (toBreak.location.blockY !in block.world.minHeight..block.world.maxHeight) {
                    continue
                }

                if (blacklist.matches(toBreak)) {
                    continue
                }

                if (whitelist != null) {
                    if (!whitelist.matches(toBreak)) {
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

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }
}