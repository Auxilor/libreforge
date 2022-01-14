package com.willfp.libreforge.triggers.wrappers

import com.willfp.libreforge.triggers.WrappedCancellableEvent
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

interface WrappedDropEvent<out T> : WrappedCancellableEvent<T> where T : Event, T : Cancellable {
    var modifier: (ItemStack) -> Pair<ItemStack, Int>

    val items: Collection<ItemStack>
}

class WrappedBlockDropEvent(
    private val event: BlockDropItemEvent
) : WrappedDropEvent<BlockDropItemEvent> {
    override var modifier: (ItemStack) -> Pair<ItemStack, Int> = { Pair(it, 0) }

    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }

    override val items: Collection<ItemStack>
        get() = event.items.map { it.itemStack }
}

class WrappedEntityDropEvent(
    private val event: EntityDeathEvent
) : WrappedDropEvent<EntityDeathEvent> {
    override var modifier: (ItemStack) -> Pair<ItemStack, Int> = { Pair(it, 0) }

    override var isCancelled: Boolean
        get() {
            return event.isCancelled
        }
        set(value) {
            event.isCancelled = value
        }

    override val items: Collection<ItemStack>
        get() = event.drops
}
