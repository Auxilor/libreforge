package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.libreforge.effects.EffectBlock
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Provides the holders that are held by a player.
 */
interface HolderProvider {
    /**
     * Provide the holders.
     */
    fun provide(player: Player): Collection<Holder>
}

class HolderProvideEvent(
    who: Player,
    val holders: Collection<Holder>
) : PlayerEvent(who) {
    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}

private val providers = mutableListOf<HolderProvider>()

/**
 * Register a new holder provider.
 */
fun registerHolderProvider(provider: HolderProvider) = providers.add(provider)

/**
 * Register a new holder provider.
 */
fun registerHolderProvider(provider: (Player) -> Collection<Holder>) = providers.add(object : HolderProvider {
    override fun provide(player: Player) = provider(player)
})

private val holderCache = Caffeine.newBuilder()
    .expireAfterWrite(4, TimeUnit.SECONDS)
    .build<UUID, Collection<Holder>>()

/**
 * The holders.
 */
val Player.holders: Collection<Holder>
    get() = holderCache.get(this.uniqueId) {
        val holders = providers.flatMap { it.provide(this) }

        Bukkit.getPluginManager().callEvent(
            HolderProvideEvent(this, holders)
        )

        holders
    }

/**
 * Invalidate holder cache to force rescan.
 */
fun Player.updateHolders() {
    holderCache.invalidate(this.uniqueId)
}

// Effects that were active on previous update
private val previousStates = DefaultHashMap<UUID, Set<EffectBlock>>(emptySet())

/**
 * Get active effects for a [player] from holders.
 */
fun Collection<Holder>.getActiveEffects(player: Player) =
    this.filter { it.conditions.areMet(player) }
        .flatMap { it.effects }
        .filter { it.conditions.areMet(player) }
        .toSet()

/**
 * Recalculate active effects.
 */
fun Player.calculateActiveEffects() =
    this.holders.getActiveEffects(this)

/**
 * The active effects.
 */
val Player.activeEffects: Set<EffectBlock>
    get() = previousStates[this.uniqueId]

/**
 * Update the active effects.
 */
fun Player.updateEffects() {
    val before = this.activeEffects
    val after = this.calculateActiveEffects()

    previousStates[this.uniqueId] = after

    val added = after - before
    val removed = before - after

    for (effect in removed) {
        effect.disable(this)
    }

    for (effect in added) {
        effect.enable(this)
    }

    for (effect in after) {
        effect.reload(this)
    }
}
