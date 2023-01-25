package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.triggers.TriggerMineBlock.Companion.preventMineBlockTrigger
import org.bukkit.Material

class EffectMineVein : Effect(
    "mine_vein",
    triggers = Triggers.withParameters(
        TriggerParameter.BLOCK
    )
) {
    override val arguments = arguments {
        require("limit", "You must specify the most blocks to break!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        if (block.hasMetadata("block-ignore")) {
            return
        }

        val player = data.player ?: return

        val limit = config.getIntFromExpression("limit", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return
        }

        val whitelist = config.getStringsOrNull("blocks")
            ?.mapNotNull { Material.matchMaterial(it.uppercase()) }
            ?: listOf(block.type)

        val blocks = BlockUtils.getVein(
            block,
            whitelist,
            limit
        )

        player.runExempted {
            for (toBreak in blocks) {
                if (!AntigriefManager.canBreakBlock(player, toBreak)) {
                    continue
                }

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
