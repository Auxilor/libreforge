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
import org.bukkit.Material
import org.bukkit.block.Block

object EffectMineRadius : MineBlockEffect<NoCompileData>("mine_radius") {
    override val description = "Mines all blocks in a cube radius around the triggered block."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius to break!",
            description = "The radius of blocks to break around the triggered block. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "prevent_trigger",
            description = "Whether breaking these blocks should prevent triggering further effects.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "disable_on_sneak",
            description = "Whether the effect should be disabled while the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "whitelist",
            description = "A list of blocks that are allowed to be broken. If omitted, all blocks are eligible.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "blacklisted_blocks",
            description = "A list of blocks that should never be broken by this effect.",
            type = ArgType.BLOCK_LIST,
            default = "[]"
        )
        optional(
            "check_hardness",
            description = "Whether blocks harder than the triggered block should be skipped.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)

        val preventTriggers = config.getBool("prevent_trigger")

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

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

                    if (blacklist.matches(toBreak)) {
                        continue
                    }

                    if (whitelist != null) {
                        if (!whitelist.matches(toBreak)) {
                            continue
                        }
                    }

                    if (config.getBoolOrNull("check_hardness") != false) {
                        if (Blocks.hardness(toBreak) < 0 || Blocks.hardness(toBreak) > Blocks.hardness(block)) {
                            continue
                        }
                    }

                    if (Blocks.hardness(toBreak) < 0) {
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

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }
}
