package com.willfp.libreforge.triggers

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
    block(dispatcher)
}

/**
 * A dispatcher for a player.
 */
class PlayerDispatcher(
    override val dispatcher: Player
) : Dispatcher<Player> {
    override val uuid
        get() = dispatcher.uniqueId
}

/**
 * A dispatcher for the global scope.
 */
object GlobalDispatcher : Dispatcher<Unit> {
    override val uuid = UUID(0, 0)
    override val dispatcher = Unit
}
