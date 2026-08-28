package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material
import org.bukkit.block.Block
import kotlin.math.abs

@Deprecated("Use mine_radius with depth: 1 instead")
object EffectMineRadiusOneDeep : MineBlockEffect<NoCompileData>("mine_radius_one_deep") {
    override val description = "Mines blocks in a radius around the triggered block, only one layer deep in the direction the player is facing."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius to break!",
            description = "The radius of blocks to break in the flat layer. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "3 + %level% / 20"
        )
        optional(
            "prevent_trigger",
            description = "Whether the broken blocks should fire further libreforge triggers. " +
                    "true breaks them silently, firing no events at all; keep_events breaks them normally, " +
                    "so drops and block events still happen, but stops the mine_block trigger from running again.",
            type = ArgType.STRING,
            default = "false",
            choices = listOf("false", "true", "keep_events")
        )
        optional(
            "disable_on_sneak",
            description = "Whether the effect should be disabled while the player is sneaking.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "no_corners",
            description = "Whether corner blocks at the edge of the radius should be excluded.",
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
        optional(
            "use_blockface",
            description = "Whether to orient the layer by the face of the block the player is " +
                    "looking at, rather than by their look direction. Falls back to the look " +
                    "direction if the player isn't looking at the triggered block.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        val player = data.player ?: return false

        val radius = config.getIntFromExpression("radius", data)

        val preventTriggers = preventTriggerMode(config)

        if (player.isSneaking && config.getBool("disable_on_sneak")) {
            return false
        }

        val whitelist = config.getStringsOrNull("whitelist")?.map { Blocks.lookup(it) }
        val blacklist = config.getFormattedStrings("blacklisted_blocks", data).map { Blocks.lookup(it) }

        val blocks = mutableSetOf<Block>()

        val ignoreVector = resolveForwardAxis(config, player, block)

        for (x in (-radius..radius)) {
            for (y in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    if (ignoreVector.x != 0.0 && x != 0) {
                        continue
                    }

                    if (ignoreVector.y != 0.0 && y != 0) {
                        continue
                    }

                    if (ignoreVector.z != 0.0 && z != 0) {
                        continue
                    }

                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    if (config.getBool("no_corners")) {
                        val atXCorner = abs(x) == radius
                        val atYCorner = abs(y) == radius
                        val atZCorner = abs(z) == radius

                        if (atXCorner && atYCorner
                            || atXCorner && atZCorner
                            || atYCorner && atZCorner
                        ) {
                            continue
                        }
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
                        if (Blocks.hardness(toBreak) > Blocks.hardness(block)) {
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

        data.breakBlocksSafely(blocks, preventTriggers)

        return true
    }
}
