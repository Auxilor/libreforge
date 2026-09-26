package com.willfp.libreforge

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

/**
 * Default polling intervals per kind of dispatcher.
 */
object HolderPolling {
    /**
     * The default maximum age of a provider's answer for players and the global dispatcher, in ticks.
     */
    const val PLAYER_MAX_AGE = 80

    /**
     * How often players' and the global dispatcher's conditions are polled, in ticks.
     */
    const val PLAYER_CONDITION_MAX_AGE = 20

    /**
     * `refresh.entities.interval`, updated on reload.
     */
    @Volatile
    var entityInterval = 60
        internal set

    /**
     * The default maximum age of a provider's answer for a [dispatcher], in ticks: 80 for players
     * and the global dispatcher, `refresh.entities.interval` for mobs and NPCs.
     */
    fun defaultMaxAge(dispatcher: Dispatcher<*>): Int =
        if (dispatcher.isPolledAsEntity) entityInterval else PLAYER_MAX_AGE

    /**
     * How often the conditions of a [dispatcher] are polled, in ticks.
     */
    internal fun conditionMaxAge(dispatcher: Dispatcher<*>): Int =
        if (dispatcher.isPolledAsEntity) entityInterval else PLAYER_CONDITION_MAX_AGE
}

/**
 * If this is a real (non-NPC) player.
 */
internal val Player.isRealPlayer: Boolean
    get() = !this.hasMetadata("NPC")

internal val Dispatcher<*>.isPolledAsEntity: Boolean
    get() {
        val entity = this.dispatcher as? LivingEntity ?: return false
        return entity !is Player || !entity.isRealPlayer
    }
