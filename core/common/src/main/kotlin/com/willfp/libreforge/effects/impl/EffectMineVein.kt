package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.libreforge.ArgType
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
    override val description = "Mines an entire connected vein of the same block type up to a configurable limit."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "limit",
            "You must specify the most blocks to break!",
            description = "The maximum number of connected blocks to break in one vein. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "blocks",
            description = "A list of block types to consider part of the vein. Defaults to the same type as the mined block.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "prevent_trigger",
            description = "Whether to prevent the vein blocks from re-triggering this effect.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "disable_on_sneak",
            description = "Whether to disable vein mining when the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: FilterList): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val limit = config.getIntFromExpression("limit", data)

        val preventTriggers = config.getBool("prevent_trigger")

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("blocks")
            ?.mapNotNull { Blocks.lookup(it) } ?: listOf(Blocks.getBlock(block))

        val blocks = BlockUtils.getVein(
            block,
            whitelist,
            limit
        )
            .filter { AntigriefManager.canBreakBlock(player, it) }
            .filter { compileData.isMet(data.copy(block = it)) }

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): FilterList {
        return Filters.compile(
            config.getSubsection("filters"),
            context.with("filters")
        )
    }
}
