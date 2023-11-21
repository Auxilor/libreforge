package com.willfp.libreforge

import io.papermc.paper.math.Position.block
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.UUID

/**
 * A dispatcher represents the object that dispatched a trigger (e.g. a player).
 */
interface Dispatcher<T> {
    /**
     * The UUID of the dispatcher.
     */
    val uuid: UUID

    /**
     * The dispatcher itself.
     */
    val dispatcher: T

    /**
     * The location of the dispatcher.
     */
    val location: Location?
}

/**
 * Get the dispatcher as a specific type.
 */
inline fun <reified T> Dispatcher<*>.get(): T? {
    return this.dispatcher as? T
}

/**
 * Check if the dispatcher is a specific type.
 */
inline fun <reified T> Dispatcher<*>.isType(): Boolean {
    return this.dispatcher is T
}

/**
 * Run a block if the dispatcher is a specific type.
 */
inline fun <reified T> Dispatcher<*>.ifType(block: (T) -> Unit) {
    val dispatcher = this.dispatcher as? T ?: return
    return block(dispatcher)
}

/**
 * A dispatcher for an entity.
 */
class EntityDispatcher(
    override val dispatcher: Entity
) : Dispatcher<Entity> {
    override val uuid
        get() = dispatcher.uniqueId

    override val location
        get() = dispatcher.location
}

/**
 * A dispatcher for the global scope.
 */
object GlobalDispatcher : Dispatcher<Unit> {
    override val uuid = UUID(0, 0)
    override val dispatcher = Unit
    override val location = null
}
