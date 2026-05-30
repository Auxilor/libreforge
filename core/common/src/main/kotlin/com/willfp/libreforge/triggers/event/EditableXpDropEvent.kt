package com.willfp.libreforge.triggers.event

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * What caused this XP drop event to be fired.
 */
enum class XpDropCause {
    /** A block was broken and dropped XP. */
    BLOCK,

    /** An entity died and dropped XP. */
    ENTITY
}

/**
 * Contextual information attached to an [EditableXpDropEvent].
 * All fields are nullable; populate only what is relevant for the cause.
 */
data class XpDropContext(
    /** The player responsible for this XP drop (miner, killer…). */
    val player: Player? = null,

    /** The block that was broken (only relevant for [XpDropCause.BLOCK]). */
    val block: Block? = null,

    /** The entity that died (only relevant for [XpDropCause.ENTITY]). */
    val entity: Entity? = null
)

/**
 * A unified, cause-agnostic XP drop event.
 *
 * [xp] is the editable amount, initialised from the source event. Cancelling
 * this event sets the effective amount to zero via [finalXp]; it deliberately
 * does NOT cancel the originating block break / entity death.
 *
 * @param initialXp     The XP that would drop before any modification.
 * @param cause         Why this XP drop is happening.
 * @param context       Extra contextual data (player, block, entity).
 * @param dropLocation  Where the XP lands.
 */
class EditableXpDropEvent(
    initialXp: Int,
    val cause: XpDropCause,
    val context: XpDropContext,
    val dropLocation: Location
) : Event(), Cancellable {

    /** The editable XP amount. Effects may read/write this directly. */
    var xp: Int = initialXp

    /** Snapshot of the XP as it was when this event was constructed. */
    @Suppress("unused")
    val originalXp: Int = initialXp

    private var cancelled = false

    // ── Convenience accessors forwarded from context ──────
    val player: Player? get() = context.player
    val block: Block? get() = context.block
    val entity: Entity? get() = context.entity

    /** The XP to actually drop: zero if cancelled, otherwise [xp]. */
    val finalXp: Int
        get() = if (cancelled) 0 else xp

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun getHandlers(): HandlerList = handlerList

    companion object {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        @Suppress("unused")
        fun getHandlerList(): HandlerList = handlerList
    }
}
