package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.eco.util.simplify
import com.willfp.libreforge.BlockBreaker
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

/**
 * How the extra blocks broken by a mine effect interact with triggers.
 */
enum class PreventTriggerMode {
    /**
     * Break the blocks as the player, firing all events and triggers.
     *
     * Other mine effects are still stopped from re-triggering, to prevent infinite recursion.
     */
    NONE,

    /**
     * Break the blocks without firing any events at all.
     */
    ALL,

    /**
     * Break the blocks as the player, firing all events, but stop the mine_block trigger from
     * dispatching for them.
     *
     * Drops, block break events, and the triggers derived from them (eg block_item_drop) still
     * fire, so block regeneration plugins and effect chains bound to those triggers keep working.
     */
    KEEP_EVENTS
}

abstract class MineBlockEffect<T : Any>(id: String) : Effect<T>(id) {
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

    /**
     * Break [blocks] the way the dispatcher of this trigger wants them broken.
     *
     * A dispatcher that owns the blocks it mines - a minion, a machine - implements
     * [BlockBreaker] to take the drops and the removal itself. Anything else falls
     * through to the player, which is every dispatcher that exists today.
     */
    protected fun TriggerData.breakBlocksSafely(blocks: Collection<Block>, mode: PreventTriggerMode) {
        val breaker = this.dispatcher as? BlockBreaker

        if (breaker != null) {
            breaker.breakBlocks(blocks, this)
            return
        }

        this.player?.breakBlocksSafely(blocks, mode)
    }

    protected fun Player.breakBlocksSafely(blocks: Collection<Block>, mode: PreventTriggerMode) {
        if (plugin.configYml.getBool("effects.use-setblock-break")) {
            blocks.forEach { it.type = Material.AIR }
            return
        }

        if (mode == PreventTriggerMode.ALL) {
            blocks.forEach { it.breakNaturally() }
            return
        }

        this.runExempted {
            for (block in blocks) {
                if (block.world != this.world) {
                    continue
                }

                block.setMetadata(ignoreKey, plugin.createMetadataValue(true))

                if (mode == PreventTriggerMode.KEEP_EVENTS) {
                    block.setMetadata(preventTriggerKey, plugin.createMetadataValue(true))
                }

                this.breakBlock(block)

                block.removeMetadata(ignoreKey, plugin)
                block.removeMetadata(preventTriggerKey, plugin)
            }
        }
    }

    @Deprecated("Use the PreventTriggerMode overload instead.")
    protected fun Player.breakBlocksSafely(blocks: Collection<Block>, preventTriggers: Boolean = false) =
        breakBlocksSafely(
            blocks,
            if (preventTriggers) PreventTriggerMode.ALL else PreventTriggerMode.NONE
        )

    companion object {
        internal const val ignoreKey = "blockbreakevent-ignore"
        internal const val preventTriggerKey = "blockbreakevent-prevent-trigger"

        /**
         * Read the prevent_trigger mode from a [config].
         *
         * Anything other than the literal string keep_events is read with getBool, exactly as
         * before, so existing configs keep the behaviour they already had.
         */
        @JvmStatic
        fun preventTriggerMode(config: Config) = when {
            config.get("prevent_trigger")?.toString()?.lowercase() == "keep_events" ->
                PreventTriggerMode.KEEP_EVENTS

            config.getBool("prevent_trigger") -> PreventTriggerMode.ALL

            else -> PreventTriggerMode.NONE
        }
    }
}
