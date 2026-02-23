package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block

object EffectMineRadius : MineBlockEffect<NoCompileData>("mine_radius") {
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

        val blocks = mutableSetOf<Block>()

        for (x in (-radius..radius)) {
            for (y in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
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
                        if (toBreak.type.hardness < 0 || toBreak.type.hardness > block.type.hardness) {
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

        val useSetBlockBreak = config.getBool("prevent_trigger")
        if (useSetBlockBreak) {
            blocks.forEach { it.breakNaturally() }
        } else {
            player.breakBlocksSafely(blocks)
        }

        return true
    }
}
