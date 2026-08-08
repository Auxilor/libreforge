package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.eco.util.simplify
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class MineBlockEffect<T : Any>(id: String) : Effect<T>(id) {
    private val ignoreKey = "blockbreakevent-ignore"

    // Slightly beyond the furthest a player can reach a block from, so that the block face
    // raytrace can't miss but also doesn't scan needlessly far.
    private val reachDistance = 7.0

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun shouldTrigger(config: Config, data: TriggerData, compileData: T): Boolean {
        val block = data.block ?: data.location?.block ?: return false
        return !block.hasMetadata(ignoreKey)
    }

    /**
     * The axes used to orient a directional mining effect, in block space.
     *
     * [forward] points into the block the player is mining, [up] and [right] span the plane
     * perpendicular to it. All three are unit vectors along a single axis.
     */
    protected class MiningAxes(
        val forward: Vector,
        val up: Vector,
        val right: Vector
    )

    /**
     * Get the axis pointing into [triggerBlock] for a [player].
     *
     * If the use_blockface argument is enabled, this is taken from the face of the block the
     * player is looking at, otherwise it's taken from the player's look direction. The look
     * direction is also used as a fallback if the player isn't looking at the trigger block,
     * which happens when the effect is run from a trigger other than mine_block.
     */
    protected fun resolveForwardAxis(config: Config, player: Player, triggerBlock: Block): Vector {
        if (config.getBool("use_blockface")) {
            val ray = player.rayTraceBlocks(reachDistance, FluidCollisionMode.NEVER)
            val face = ray?.hitBlockFace

            if (ray?.hitBlock == triggerBlock && face != null && face != BlockFace.SELF) {
                // The face points out of the block, towards the player, so it has to be flipped.
                return face.direction.multiply(-1.0)
            }
        }

        return player.location.direction.simplify()
    }

    /**
     * Get the full set of [MiningAxes] for a [player] mining [triggerBlock].
     */
    protected fun resolveMiningAxes(config: Config, player: Player, triggerBlock: Block): MiningAxes {
        val forward = resolveForwardAxis(config, player, triggerBlock)
        val worldUp = Vector(0.0, 1.0, 0.0)

        // When mining vertically there's no meaningful world up, so the player's horizontal
        // look direction is used to orient the plane instead.
        return if (forward.y != 0.0) {
            val direction = player.location.direction
            val up = Vector(direction.x, 0.0, direction.z).simplify()

            MiningAxes(forward, up, up.clone().crossProduct(worldUp).simplify())
        } else {
            MiningAxes(forward, worldUp.clone(), forward.clone().crossProduct(worldUp).simplify())
        }
    }

    protected fun Player.breakBlocksSafely(blocks: Collection<Block>, preventTriggers: Boolean = false) {
        if (plugin.configYml.getBool("effects.use-setblock-break")) {
            blocks.forEach { it.type = Material.AIR }
        } else if (preventTriggers) {
            blocks.forEach { it.breakNaturally() }
        } else {
            this.runExempted {
                for (block in blocks) {
                    if (block.world != this.world) {
                        continue
                    }

                    block.setMetadata(ignoreKey, plugin.createMetadataValue(true))
                    this.breakBlock(block)
                    block.removeMetadata(ignoreKey, plugin)
                }
            }
        }
    }
}
