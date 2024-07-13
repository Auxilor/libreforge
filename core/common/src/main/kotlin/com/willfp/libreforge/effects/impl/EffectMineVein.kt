package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material

object EffectMineVein : MineBlockEffect<FilterList>("mine_vein") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("limit", "You must specify the most blocks to break!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: FilterList): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val limit = config.getIntFromExpression("limit", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("blocks")
            ?.mapNotNull { Material.matchMaterial(it.uppercase()) }
            ?: listOf(block.type)

        val blocks = BlockUtils.getVein(
            block,
            whitelist,
            limit
        )
            .filter { AntigriefManager.canBreakBlock(player, it) }
            .filter { compileData.isMet(data.copy(block = it)) }

        player.breakBlocksSafely(blocks)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): FilterList {
        return Filters.compile(
            config.getSubsection("filters"),
            context.with("filters")
        )
    }
}
