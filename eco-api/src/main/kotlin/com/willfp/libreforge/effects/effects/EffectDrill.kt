package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.VectorUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.runExempted
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.block.Block


class EffectDrill : Effect(
    "drill",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        if (block.hasMetadata("block-ignore")) {
            return
        }

        val player = data.player ?: return

        val amount = config.getIntFromExpression("amount")

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
            BlockUtils.breakBlock(player, toBreak)
            toBreak.removeMetadata("block-ignore", plugin)
        }

        player.runExempted {
            for (toBreak in blocks) {
                toBreak.setMetadata("block-ignore", plugin.metadataValueFactory.create(true))
                BlockUtils.breakBlock(player, toBreak)
                toBreak.removeMetadata("block-ignore", plugin)
            }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of blocks to break!"
            )
        )

        if (!config.has("check_hardness")) violations.add(
            ConfigViolation(
                "check_hardness",
                "You must specify if block hardness should be checked!"
            )
        )

        return violations
    }
}
