package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.runExempted
import com.willfp.eco.util.simplify
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock.Companion.preventMineBlockTrigger
import org.bukkit.Material
import org.bukkit.block.Block
import kotlin.math.abs

class EffectMineRadiusOneDeep : Effect(
    "mine_radius_one_deep",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("radius", "You must specify the radius to break!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        if (block.hasMetadata("block-ignore")) {
            return
        }

        val player = data.player ?: return

        val radius = config.getIntFromExpression("radius", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return
        }

        val whitelist = config.getStringsOrNull("whitelist")

        val blocks = mutableSetOf<Block>()

        val ignoreVector = player.location.direction.simplify()

        for (x in (-radius..radius)) {
            for (y in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    // Jank
                    if (ignoreVector.x != 0.0 && x != 0) {
                        continue
                    }

                    if (ignoreVector.y != 0.0 && y != 0) {
                        continue
                    }

                    if (ignoreVector.z != 0.0 && z != 0) {
                        continue
                    }
                    // End Jank

                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    if (config.getBool("no_corners")) {
                        val atXCorner = abs(x) == radius
                        val atYCorner = abs(y) == radius
                        val atZCorner = abs(z) == radius

                        if (atXCorner && atYCorner
                            || atXCorner && atZCorner
                            || atYCorner && atZCorner) {
                            continue
                        }
                    }

                    val toBreak = block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )

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

        player.runExempted {
            for (toBreak in blocks) {
                toBreak.setMetadata("block-ignore", plugin.metadataValueFactory.create(true))

                if (config.getBool("prevent_trigger")) {
                    toBreak.preventMineBlockTrigger()
                }

                player.breakBlock(toBreak)
                toBreak.removeMetadata("block-ignore", plugin)
            }
        }
    }
}
