package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.runExempted
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Material
import org.bukkit.block.Block

class EffectMineRadius : Effect(
    "mine_radius",
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

        val radius = config.getIntFromExpression("radius")

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return
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
                BlockUtils.breakBlock(player, toBreak)
                toBreak.removeMetadata("block-ignore", plugin)
            }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius to break!"
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
