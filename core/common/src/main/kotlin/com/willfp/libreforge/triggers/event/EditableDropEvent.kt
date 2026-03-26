package com.willfp.libreforge.triggers.event

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

/*
 * An ItemStack paired with accumulated XP from drop modifiers.
 */
data class DropResult(
    val item: ItemStack,
    val xp: Int
)

/**
 * Transforms a drop: the returned XP is accumulated; item mutations should
 * be done in-place since the item reference is shared with the event's drop list.
 */
typealias DropModifier = (ItemStack) -> DropResult

/**
 * Apply all modifiers to [item], accumulating XP.  Item mutations happen
 * in-place; only the XP from each modifier's result is used.
 */
fun Collection<DropModifier>.modify(item: ItemStack): DropResult {
    var xp = 0
    for (modifier in this) {
        xp += modifier(item).xp
    }
    return DropResult(item, xp)
}

/**
 * What caused this drop event to be fired.
 */
enum class DropCause {
    /** A block was broken and dropped items. */
    BLOCK,

    /** An entity died and dropped items. */
    ENTITY,

    /** An entity was sheared. */
    SHEAR,

    /** A player caught a fish. */
    FISHING,

    /** Any other / custom source. */
    CUSTOM
}

/**
 * Contextual information attached to an [EditableDropEvent].
 * All fields are nullable; populate only what is relevant for the cause.
 */
data class DropContext(
    /** The player responsible for this drop (miner, killer, fisherman…). */
    val player: Player? = null,

    /** The block that was broken (only relevant for [DropCause.BLOCK]). */
    val block: Block? = null,

    /** The entity that dropped/was sheared (only relevant for [DropCause.ENTITY] / [DropCause.SHEAR]). */
    val entity: Entity? = null,

    /** The tool used to produce this drop. */
    val tool: ItemStack? = null
)

/**
 * A unified, cause-agnostic drop event.
 *
 * All drops are stored as a [MutableList] so effects can add, remove, or
 * reorder items directly.  [DropModifier]s are layered on top and are applied
 * lazily when [items] is read (they accumulate XP and mutate item stacks
 * in-place).
 *
 * @param initialDrops  The items that will be dropped before any modification.
 * @param cause         Why this drop is happening.
 * @param context       Extra contextual data (player, block, entity, tool).
 * @param dropLocation  Where the items land.
 * @param cancellable   Optional delegate used to implement [isCancelled] /
 *                      [setCancelled].  Pass the originating Bukkit event when
 *                      available.
 */
class EditableDropEvent(
    initialDrops: List<ItemStack>,
    val cause: DropCause,
    val context: DropContext,
    val dropLocation: Location,
    private val cancellable: Cancellable? = null
) : Event(), Cancellable {

    /** Live, mutable drop list.  Modify directly or via [removeItem]. */
    val drops: MutableList<ItemStack> = initialDrops.toMutableList()

    /**
     * Snapshot of the drops as they were when this event was constructed,
     * before any effect/modifier ran.
     */
    @Suppress("unused")
    val originalItems: List<ItemStack> = initialDrops.toList()

    private val modifiers = mutableListOf<DropModifier>()

    // ── Convenience accessors forwarded from context (backwards compat) ──────

    /** Shorthand for [DropContext.player]. */
    val player: Player? get() = context.player

    /** Shorthand for [DropContext.block]. */
    val block: Block? get() = context.block

    /** Shorthand for [DropContext.entity]. */
    val entity: Entity? get() = context.entity

    /** Shorthand for [DropContext.tool]. */
    @Suppress("unused")
    val tool: ItemStack? get() = context.tool

    /**
     * Register a modifier that will be applied to every item in [drops] when
     * [items] is read.  Modifiers accumulate XP; item mutations should be
     * done in-place on the passed [ItemStack].
     */
    fun addModifier(modifier: DropModifier) {
        modifiers += modifier
    }

    /**
     * The current [drops] with all registered modifiers applied.
     * XP values from every modifier are summed per item.
     */
    val items: List<DropResult>
        get() = drops.map { modifiers.modify(it) }

    /**
     * Remove [item] from [drops] by equality.
     * For identity-safe removal iterate [drops] directly.
     */
    fun removeItem(item: ItemStack) {
        drops.remove(item)
    }

    override fun isCancelled(): Boolean = cancellable?.isCancelled ?: false

    override fun setCancelled(cancel: Boolean) {
        cancellable?.isCancelled = cancel
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

