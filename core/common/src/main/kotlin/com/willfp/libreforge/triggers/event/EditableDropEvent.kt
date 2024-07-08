package com.willfp.libreforge.triggers.event

import com.willfp.eco.util.toSingletonList
import org.bukkit.Location
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack


/*
An ItemStack becomes a DropResult.
 */
data class DropResult(
    val item: ItemStack,
    val xp: Int
)

/**
 * Turns an ItemStack into a DropResult.
 */
typealias DropModifier = (ItemStack) -> DropResult

/**
 * Modify an ItemStack.
 */
fun Collection<DropModifier>.modify(item: ItemStack): DropResult {
    var xp = 0

    for (modifier in this) {
        xp += modifier(item).xp
    }

    return DropResult(item, xp)
}

/**
 * A 'mock' drop event providing a common API by which to modify drops.
 */
abstract class EditableDropEvent : Event(), Cancellable {
    abstract fun addModifier(modifier: DropModifier)

    /**
     * Items before any modifiers are applied.
     */
    abstract val originalItems: List<ItemStack>

    /**
     * Items after modifiers are applied.
     */
    abstract val items: List<DropResult>

    /**
     * The location of the drops.
     */
    abstract val dropLocation: Location

    /**
     * Remove an item.
     */
    abstract fun removeItem(item: ItemStack)

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}

class EditableEntityDropEvent(
    private val event: EntityDeathEvent
) : EditableDropEvent() {
    private val modifiers = mutableListOf<DropModifier>()

    override fun addModifier(modifier: DropModifier) {
        modifiers += modifier
    }

    override val originalItems: List<ItemStack>
        get() = event.drops

    override val items: List<DropResult>
        get() = originalItems.map { modifiers.modify(it) }

    override val dropLocation: Location
        get() = event.entity.location

    override fun removeItem(item: ItemStack) {
        event.drops.remove(item)
    }

    override fun isCancelled(): Boolean {
        return event.isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        event.isCancelled = p0
    }
}

class EditableBlockDropEvent(
    private val event: BlockDropItemEvent
) : EditableDropEvent() {
    private val modifiers = mutableListOf<DropModifier>()

    override fun addModifier(modifier: DropModifier) {
        modifiers += modifier
    }

    override val originalItems: List<ItemStack>
        get() = event.items.map { it.itemStack }

    override val items: List<DropResult>
        get() = originalItems.map { modifiers.modify(it) }

    override val dropLocation: Location
        get() = event.items.first().location

    override fun removeItem(item: ItemStack) {
        event.items.removeIf { it.itemStack == item }
    }

    override fun isCancelled(): Boolean {
        return event.isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        event.isCancelled = p0
    }
}

class EditablePlayerDropEvent(
    private val event: PlayerDropItemEvent
): EditableDropEvent() {
    private val modifiers = mutableListOf<DropModifier>()

    override fun addModifier(modifier: DropModifier) {
        modifiers += modifier
    }

    override val originalItems: List<ItemStack>
        get() = event.itemDrop.itemStack.toSingletonList()

    override val items: List<DropResult>
        get() = originalItems.map { modifiers.modify(it) }

    override val dropLocation: Location
        get() = event.itemDrop.location

    override fun removeItem(item: ItemStack) {
        if (event.itemDrop.itemStack == item) {
            event.itemDrop.remove()
        }
    }

    override fun isCancelled(): Boolean {
        return event.isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        event.isCancelled = p0
    }
}
