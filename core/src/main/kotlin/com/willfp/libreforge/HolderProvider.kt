package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.map.defaultMap
import com.willfp.libreforge.effects.EffectBlock
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Provides the holders that are held by a player.
 */
interface HolderProvider {
    /**
     * Provide the holders.
     */
    fun provide(player: Player): Collection<ProvidedHolder>
}

class HolderProvideEvent(
    who: Player,
    val holders: Collection<ProvidedHolder>
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
fun registerHolderProvider(provider: (Player) -> Collection<ProvidedHolder>) =
    registerHolderProvider(object : HolderProvider {
        override fun provide(player: Player) = provider(player)
    })

private val playerRefreshFunctions = mutableListOf<(Player) -> Unit>()

/**
 * Register a function to be called when a player's holders are refreshed.
 */
fun registerPlayerRefreshFunction(function: (Player) -> Unit) {
    playerRefreshFunctions += function
}

/**
 * Update holders, effects, and call refresh functions.
 */
fun Player.refreshHolders() {
    playerRefreshFunctions.forEach { it(this) }
    this.updateHolders()
    this.updateEffects()
}

private val holderPlaceholderProviders = mutableListOf<(ProvidedHolder) -> Collection<NamedValue>>()

/**
 * Register a function to generate placeholders for a holder.
 */
fun registerHolderPlaceholderProvider(provider: (ProvidedHolder) -> Collection<NamedValue>) {
    holderPlaceholderProviders += provider
}

/**
 * Generate placeholders for a holder.
 */
fun ProvidedHolder.generatePlaceholders(): List<NamedValue> {
    return holderPlaceholderProviders.flatMap { it(this) }
}

private val holderCache = Caffeine.newBuilder()
    .expireAfterWrite(4, TimeUnit.SECONDS)
    .build<UUID, Collection<ProvidedHolder>>()

/**
 * The holders.
 */
val Player.holders: Collection<ProvidedHolder>
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
private val previousStates = defaultMap<UUID, Map<ProvidedHolder, Set<EffectBlock>>>(emptyMap())
private val flattenedPreviousStates = defaultMap<UUID, Set<EffectBlock>>(emptySet()) // Optimisation.

/**
 * Flatten down to purely the effects.
 */
fun Map<ProvidedHolder, Set<EffectBlock>>.flatten() = this.flatMap { it.value }.toSet()

/**
 * Map the effects to the holders that provided them.
 */
fun Map<ProvidedHolder, Set<EffectBlock>>.mapBlocksToHolders(): Map<EffectBlock, ProvidedHolder> {
    val map = mutableMapOf<EffectBlock, ProvidedHolder>()

    for ((holder, effects) in this) {
        for (effect in effects) {
            map[effect] = holder
        }
    }

    return map
}

/**
 * Get active effects for a [player] from holders mapped to the holder
 * that has provided them.
 */
fun Collection<ProvidedHolder>.getProvidedActiveEffects(player: Player): Map<ProvidedHolder, Set<EffectBlock>> {
    val map = mutableMapOf<ProvidedHolder, Set<EffectBlock>>()

    for (holder in this) {
        if (holder.holder.conditions.areMet(player, holder)) {
            map[holder] = holder.getActiveEffects(player)
        }
    }

    return map
}

/**
 * Get active effects for a [player].
 */
@Deprecated(
    "Use ProvidedHolder.getActiveEffects instead", ReplaceWith(
        "SimpleProvidedHolder(this).getActiveEffects(player)",
        "com.willfp.libreforge.SimpleProvidedHolder"
    )
)
fun Holder.getActiveEffects(player: Player) =
    SimpleProvidedHolder(this).getActiveEffects(player)

/**
 * Get active effects for a [player].
 */
fun ProvidedHolder.getActiveEffects(player: Player) =
    this.holder.effects.filter { it.conditions.areMet(player, this) }.toSet()

/**
 * Recalculate active effects.
 */
fun Player.calculateActiveEffects() =
    this.holders.getProvidedActiveEffects(this)

/**
 * The active effects.
 */
val Player.activeEffects: Set<EffectBlock>
    get() = flattenedPreviousStates[this.uniqueId]

/**
 * The active effects mapped to the holder that provided them.
 */
val Player.providedActiveEffects: Map<ProvidedHolder, Set<EffectBlock>>
    get() = previousStates[this.uniqueId]

/**
 * Update the active effects.
 */
fun Player.updateEffects() {
    val before = this.providedActiveEffects
    val after = this.calculateActiveEffects()

    previousStates[this.uniqueId] = after
    flattenedPreviousStates[this.uniqueId] = after.flatten()

    val beforeF = before.flatten()
    val afterF = after.flatten()

    val beforeMap = before.mapBlocksToHolders().toNotNullMap()
    val afterMap = after.mapBlocksToHolders().toNotNullMap()

    val added = afterF - beforeF
    val removed = beforeF - afterF
    val toReload = afterF - added

    for (effect in removed) {
        effect.disable(this, beforeMap[effect])
    }

    for (effect in added) {
        effect.enable(this, afterMap[effect])
    }

    // Reloading is now done by disabling all, then enabling all. Effect#reload is deprecated.

    for (effect in toReload) {
        effect.disable(this, afterMap[effect], isReload = true)
    }

    for (effect in toReload) {
        effect.enable(this, afterMap[effect], isReload = true)
    }
}
