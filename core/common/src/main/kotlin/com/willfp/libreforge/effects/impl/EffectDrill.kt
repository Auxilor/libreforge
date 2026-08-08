package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.block.Block


object EffectDrill : MineBlockEffect<NoCompileData>("drill") {
    override val description = "Breaks a line of blocks in front of the player in their look direction."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of blocks to break!",
            description = "The number of blocks to break in a line. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "3 + %level%"
        )
        require(
            "check_hardness",
            "You must specify if hardness should be checked!",
            description = "If true, only blocks with hardness ≤ the trigger block are broken.",
            type = ArgType.BOOLEAN
        )
        optional(
            "disable_on_sneak",
            description = "If true, the drill effect is disabled while the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "prevent_trigger",
            description = "Whether the additional blocks should fire further libreforge triggers. " +
                    "true breaks them silently, firing no events at all; keep_events breaks them normally, " +
                    "so drops and block events still happen, but stops the mine_block trigger from running again.",
            type = ArgType.STRING,
            default = "false",
            choices = listOf("false", "true", "keep_events")
        )
        optional(
            "whitelist",
            description = "Only these block types will be broken by the drill.",
            type = ArgType.BLOCK_LIST
        )
        optional(
            "blacklisted_blocks",
            description = "These block types will never be broken by the drill.",
            type = ArgType.BLOCK_LIST
        )
        optional(
            "use_blockface",
            description = "Whether to drill into the face of the block the player is looking at, " +
                    "rather than in their look direction. Falls back to the look direction if the " +
                    "player isn't looking at the trigger block.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        val player = data.player ?: return false

        val amount = config.getIntFromExpression("amount", data)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

        val preventTriggers = preventTriggerMode(config)

        val blocks = mutableSetOf<Block>()

        val forwardAxis = resolveForwardAxis(config, player, block)

        for (i in 1..amount) {
            val offset = forwardAxis.clone().multiply(i)
            val toBreak = block.world.getBlockAt(block.location.clone().add(offset))

            if (blacklist.matches(toBreak)) {
                continue
            }

            if (whitelist != null) {
                if (!whitelist.matches(toBreak)) {
                    continue
                }
            }

            if (config.getBool("check_hardness")) {
                if (Blocks.hardness(toBreak) > Blocks.hardness(block)) {
                    continue
                }
            }

            if (!AntigriefManager.canBreakBlock(player, toBreak)) {
                continue
            }

            if (Blocks.hardness(toBreak) < 0) {
                continue
            }

            blocks.add(toBreak)
        }

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }
}
