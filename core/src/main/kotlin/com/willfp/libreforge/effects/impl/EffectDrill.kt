package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.VectorUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.block.Block


class EffectDrill : Effect(
    "drill",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of blocks to break!")
        require("check_hardness", "You must specify if hardness should be checked!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        if (block.hasMetadata("block-ignore")) {
            return
        }

        val player = data.player ?: return

        val amount = config.getIntFromExpression("amount", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return
        }

        val whitelist = config.getStringsOrNull("whitelist")

        val blocks = mutableSetOf<Block>()

        for (i in 1..amount) {
            val simplified = VectorUtils.simplifyVector(player.location.direction.normalize()).multiply(i)
            val toBreak = block.world.getBlockAt(block.location.clone().add(simplified))

            if (config.getStrings("blacklisted_blocks").containsIgnoreCase(toBreak.type.name)) {
                continue
            }

            if (whitelist != null) {
                if (!whitelist.containsIgnoreCase(toBreak.type.name)) {
                    continue
                }
            }

            if (config.getBool("check_hardness")) {
                if (toBreak.type.hardness > block.type.hardness) {
                    continue
                }
            }

            if (!AntigriefManager.canBreakBlock(player, toBreak)) {
                continue
            }

            blocks.add(toBreak)
        }

        player.runExempted {
            for (toBreak in blocks) {
                toBreak.setMetadata("block-ignore", plugin.metadataValueFactory.create(true))
                player.breakBlock(toBreak)
                toBreak.removeMetadata("block-ignore", plugin)
            }
        }
    }
}
