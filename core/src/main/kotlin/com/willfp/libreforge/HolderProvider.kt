package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.map.listMap
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

class HolderEnableEvent(
    who: Player,
    val holder: ProvidedHolder,
    val newHolders: Collection<ProvidedHolder>
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

class HolderDisableEvent(
    who: Player,
    val holder: ProvidedHolder,
    val previousHolders: Collection<ProvidedHolder>
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

/**
 * EffectBlocks provided by a holder.
 *
 * Previously this was done with maps but this led to bugs where
 * multiple identical holders were not recognised.
 */
data class ProvidedEffectBlocks(
    val holder: ProvidedHolder,
    val effects: Set<EffectBlock>
)

/**
 * EffectBlock provided by a holder.
 */
data class ProvidedEffectBlock(
    val effect: EffectBlock,
    val holder: ProvidedHolder
) : Comparable<ProvidedEffectBlock> {
    override fun compareTo(other: ProvidedEffectBlock): Int {
        return this.effect.weight - other.effect.weight
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

private val holderPlaceholderProviders = mutableListOf<(ProvidedHolder, Player) -> Collection<NamedValue>>()

/**
 * Register a function to generate placeholders for a holder.
 */
fun registerHolderPlaceholderProvider(provider: (ProvidedHolder) -> Collection<NamedValue>) =
    registerHolderPlaceholderProvider { providedHolder, _ -> provider(providedHolder) }

/**
 * Register a function to generate placeholders for a holder.
 */
fun registerHolderPlaceholderProvider(provider: (ProvidedHolder, Player) -> Collection<NamedValue>) {
    holderPlaceholderProviders += provider
}

/**
 * Generate placeholders for a holder.
 */
fun ProvidedHolder.generatePlaceholders(player: Player): List<NamedValue> {
    return holderPlaceholderProviders.flatMap { it(this, player) }
}

private val previousHolders = mutableMapOf<UUID, Collection<ProvidedHolder>>()

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

        val old = previousHolders[this.uniqueId] ?: emptyList()

        val newID = holders.map { it.holder.id }
        val oldID = old.map { it.holder.id }

        val added = newID without oldID
        val removed = oldID without newID

        val newByID = holders.associateBy { it.holder.id }.toNotNullMap()
        val oldByID = old.associateBy { it.holder.id }.toNotNullMap()

        for (id in added) {
            Bukkit.getPluginManager().callEvent(
                HolderEnableEvent(this, newByID[id], holders)
            )
        }

        for (id in removed) {
            Bukkit.getPluginManager().callEvent(
                HolderDisableEvent(this, oldByID[id], old)
            )
        }

        previousHolders[this.uniqueId] = holders

        holders
    }

/**
 * Invalidate holder cache to force rescan.
 */
fun Player.updateHolders() {
    holderCache.invalidate(this.uniqueId)
}

/**
 * Purge previous known holders.
 */
fun Player.purgePreviousHolders() {
    previousHolders.remove(this.uniqueId)
}

// Effects that were active on previous update
private val previousStates = listMap<UUID, ProvidedEffectBlocks>()
private val flattenedPreviousStates = listMap<UUID, ProvidedEffectBlock>() // Optimisation.

/**
 * Flatten down to the effects.
 */
fun Collection<ProvidedEffectBlocks>.flatten(): List<ProvidedEffectBlock> {
    val list = mutableListOf<ProvidedEffectBlock>()

    for ((holder, effects) in this) {
        for (effect in effects) {
            list += ProvidedEffectBlock(effect, holder)
        }
    }

    return list
}

/**
 * Get active effects for a [player] from holders mapped to the holder
 * that has provided them.
 */
fun Collection<ProvidedHolder>.getProvidedActiveEffects(player: Player): List<ProvidedEffectBlocks> {
    val blocks = mutableListOf<ProvidedEffectBlocks>()

    for (holder in this) {
        if (holder.holder.conditions.areMet(player, holder)) {
            blocks += ProvidedEffectBlocks(holder, holder.getActiveEffects(player))
        }
    }

    return blocks
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
val Player.activeEffects: List<EffectBlock>
    get() = flattenedPreviousStates[this.uniqueId].map { it.effect }

/**
 * The active effects mapped to the holder that provided them.
 */
val Player.providedActiveEffects: List<ProvidedEffectBlocks>
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

    // Permanent effects also have a run order, so we need to sort them.
    val added = (afterF without beforeF).sorted()
    val removed = (beforeF without afterF).sorted()
    val toReload = (afterF without added).sorted()

    for ((effect, holder) in removed) {
        effect.disable(this, holder)
    }

    for ((effect, holder) in added) {
        effect.enable(this, holder)
    }

    // Reloading is now done by disabling all, then enabling all. Effect#reload is deprecated.
    // Since permanent effects are not allowed in chains, they are always done in the correct
    // order as mixing weights is not a concern.

    for ((effect, holder) in toReload) {
        effect.disable(this, holder, isReload = true)
    }

    for ((effect, holder) in toReload) {
        effect.enable(this, holder, isReload = true)
    }
}

/**
 * Removes all elements from the given [other] list that are contained in this list.
 *
 * Elements are only removed as many times as they are present.
 */
inline infix fun <reified T> Collection<T>.without(other: Collection<T>): List<T> {
    return other.toMutableList().let { mutableOther ->
        filter { element ->
            !mutableOther.remove(element)
        }
    }
}
