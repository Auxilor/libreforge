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
import org.bukkit.entity.Player
import kotlin.math.abs

object EffectMineRadius : MineBlockEffect<NoCompileData>("mine_radius") {
    override val description = "Mines all blocks in a cube radius around the triggered block, " +
            "or in flat layers facing the player if depth is set."
    override val categories = setOf("world")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "radius",
            "You must specify the radius to break!",
            description = "The radius of blocks to break around the triggered block. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "2 + %level% / 20"
        )
        optional(
            "depth",
            description = "How many layers deep to mine, measured from the triggered block in the " +
                    "direction the player is facing. If omitted, a full cube is mined instead. " +
                    "Set this to 1 to mine a single flat layer. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1"
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
            description = "Whether to measure depth from the face of the block the player is " +
                    "looking at, rather than from their look direction. Falls back to the look " +
                    "direction if the player isn't looking at the triggered block. Only has an " +
                    "effect when depth is set.",
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
        val blacklist = config.getStrings("blacklisted_blocks").map { Blocks.lookup(it) }

        val noCorners = config.getBool("no_corners")

        val candidates = if (config.has("depth")) {
            val depth = config.getIntFromExpression("depth", data).coerceAtLeast(1)
            layeredCandidates(config, player, block, radius, depth, noCorners)
        } else {
            cubeCandidates(block, radius, noCorners)
        }

        val blocks = mutableSetOf<Block>()

        for (toBreak in candidates) {
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

        player.breakBlocksSafely(blocks, preventTriggers)

        return true
    }

    /**
     * Every block in a cube of [radius] around [block], excluding the block itself.
     */
    private fun cubeCandidates(
        block: Block,
        radius: Int,
        noCorners: Boolean
    ): List<Block> {
        val candidates = mutableListOf<Block>()

        for (x in (-radius..radius)) {
            for (y in (-radius..radius)) {
                for (z in (-radius..radius)) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue
                    }

                    if (noCorners && isCorner(radius, x, y, z)) {
                        continue
                    }

                    candidates += block.world.getBlockAt(
                        block.location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                    )
                }
            }
        }

        return candidates
    }

    /**
     * Every block in [depth] flat layers of [radius], starting at the layer containing [block]
     * and extending in the direction the player is mining. Excludes the block itself.
     */
    private fun layeredCandidates(
        config: Config,
        player: Player,
        block: Block,
        radius: Int,
        depth: Int,
        noCorners: Boolean
    ): List<Block> {
        val axes = resolveMiningAxes(config, player, block)
        val candidates = mutableListOf<Block>()

        for (layer in 0 until depth) {
            for (up in (-radius..radius)) {
                for (right in (-radius..radius)) {
                    if (layer == 0 && up == 0 && right == 0) {
                        continue
                    }

                    if (noCorners && abs(up) == radius && abs(right) == radius) {
                        continue
                    }

                    candidates += block.world.getBlockAt(
                        block.location.clone().add(
                            axes.right.x * right + axes.up.x * up + axes.forward.x * layer,
                            axes.right.y * right + axes.up.y * up + axes.forward.y * layer,
                            axes.right.z * right + axes.up.z * up + axes.forward.z * layer
                        )
                    )
                }
            }
        }

        return candidates
    }

    private fun isCorner(radius: Int, x: Int, y: Int, z: Int): Boolean {
        val atXCorner = abs(x) == radius
        val atYCorner = abs(y) == radius
        val atZCorner = abs(z) == radius

        return atXCorner && atYCorner
                || atXCorner && atZCorner
                || atYCorner && atZCorner
    }
}
