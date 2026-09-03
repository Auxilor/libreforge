package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.BlockUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Orientable

object EffectStripLogs : Effect<FilterList>("strip_logs") {
    override val description = "Strips an entire connected group of logs up to a configurable limit."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "limit",
            "You must specify the most logs to strip!",
            description = "The maximum number of connected logs to strip at once. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "10 + %level%"
        )
        optional(
            "blocks",
            description = "A list of block types to consider part of the tree. " +
                    "Defaults to every strippable log type, so trees made of more than one type of log " +
                    "are stripped in one go.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "disable_on_sneak",
            description = "Whether to disable stripping when the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "filters",
            description = "A standard filters block restricting which logs are stripped. Each log in the " +
                    "tree is tested against these filters, and only the logs that pass are stripped.",
            type = ArgType.ANY
        )
    }

    private val strippedVariants = Material.entries
        .filter { it.isBlock }
        .mapNotNull { material ->
            val stripped = Material.getMaterial("STRIPPED_${material.name}") ?: return@mapNotNull null
            material to stripped
        }
        .toMap()

    private val strippableBlocks = strippedVariants.keys
        .mapNotNull { Blocks.lookup(it.name.lowercase()) }

    override fun onTrigger(config: Config, data: TriggerData, compileData: FilterList): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val limit = config.getIntFromExpression("limit", data)

        val whitelist = config.getStringsOrNull("blocks")
            ?.mapNotNull { Blocks.lookup(it) }
            ?.takeIf { it.isNotEmpty() }
            ?: strippableBlocks

        val logs = BlockUtils.getVein(block, whitelist, limit)
            .filter { AntigriefManager.canBreakBlock(player, it) }
            .filter { compileData.isMet(data.copy(block = it)) }

        if (logs.isEmpty()) {
            return false
        }

        for (log in logs) {
            strip(log)
        }

        return true
    }

    private fun strip(block: Block) {
        val stripped = strippedVariants[block.type] ?: return

        val previousData = block.blockData
        val strippedData = stripped.createBlockData()

        if (previousData is Orientable && strippedData is Orientable) {
            strippedData.axis = previousData.axis
        }

        block.setBlockData(strippedData, false)
    }

    override fun makeCompileData(config: Config, context: ViolationContext): FilterList {
        return Filters.compile(
            config.getSubsection("filters"),
            context.with("filters")
        )
    }
}
