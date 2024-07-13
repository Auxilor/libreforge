package com.willfp.libreforge

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.effects.EffectBlock
import com.willfp.libreforge.slot.ItemHolderFinder
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.UUID
import java.util.concurrent.TimeUnit


/**
 * Provides the holders that are held by a player.
 */
interface HolderProvider {
    /**
     * Provide the holders.
     */
    fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder>
}

/**
 * A typed holder provider.
 */
interface TypedHolderProvider<T : Holder> : HolderProvider {
    override fun provide(dispatcher: Dispatcher<*>): Collection<TypedProvidedHolder<T>>
}

class HolderProvideEvent(
    val dispatcher: Dispatcher<*>,
    val holders: Collection<ProvidedHolder>
) : Event() {
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
    val dispatcher: Dispatcher<*>,
    val holder: ProvidedHolder,
    val newHolders: Collection<ProvidedHolder>
) : Event() {
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
    val dispatcher: Dispatcher<*>,
    val holder: ProvidedHolder,
    val previousHolders: Collection<ProvidedHolder>
) : Event() {
    override fun getHandlers() = handlerList

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}

@Deprecated(
    "Use ProvidedEffectBlock instead, this is no longer used and will be removed in a future version.",
    ReplaceWith("ProvidedEffectBlock"),
    DeprecationLevel.ERROR
)
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
@Deprecated(
    "Use registerSpecificHolderProvider<Player> instead",
    ReplaceWith("registerSpecificHolderProvider<Player>(provider)"),
    DeprecationLevel.ERROR
)
fun registerHolderProvider(provider: (Player) -> Collection<ProvidedHolder>) =
    registerHolderProvider(object : HolderProvider {
        override fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> {
            dispatcher.ifType<Player> {
                return provider(it)
            }

            return emptyList()
        }
    })

/**
 * Register a new holder provider for all possible dispatchers.
 */
fun registerGenericHolderProvider(provider: (Dispatcher<*>) -> Collection<ProvidedHolder>) =
    registerHolderProvider(object : HolderProvider {
        override fun provide(dispatcher: Dispatcher<*>) = provider(dispatcher)
    })

/**
 * Register a new holder provider for a specific type of dispatcher.
 */
inline fun <reified T> registerSpecificHolderProvider(crossinline provider: (T) -> Collection<ProvidedHolder>) =
    registerHolderProvider(object : HolderProvider {
        override fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> {
            return if (dispatcher.isType<T>()) {
                provider(dispatcher.get<T>()!!)
            } else {
                emptyList()
            }
        }
    })

fun registerSlotHolderFinderAsProvider(finder: ItemHolderFinder<*>) =
    registerHolderProvider(finder.toHolderProvider())

private val refreshFunctions = mutableListOf<(Dispatcher<*>) -> Unit>()

/**
 * Register a function to be called when a dispatcher's holders are refreshed.
 */
fun registerRefreshFunction(function: (Dispatcher<*>) -> Unit) {
    refreshFunctions += function
}

@Deprecated(
    "Use registerSpecificRefreshFunction<Player> instead",
    ReplaceWith("registerSpecificRefreshFunction<Player>(function)"),
    DeprecationLevel.ERROR
)
fun registerPlayerRefreshFunction(function: (Player) -> Unit) {
    refreshFunctions += {
        it.get<Player>()?.let { player ->
            function(player)
        }
    }
}

/**
 * Register a function to be called when a dispatcher's holders are refreshed for a specific dispatcher.
 */
inline fun <reified T> registerSpecificRefreshFunction(crossinline function: (T) -> Unit) {
    registerRefreshFunction {
        it.get<T>()?.let { t ->
            function(t)
        }
    }
}

private val holderCooldown: Cache<UUID, Unit>? = null
//    plugin.configYml.getInt("refresh.cooldown").takeIf { it > 0 }?.let {
//        Caffeine.newBuilder()
//            .expireAfterWrite(it.toLong(), TimeUnit.MILLISECONDS)
//            .build()
//    }

/**
 * Update holders, effects, and call refresh functions.
 */
fun Dispatcher<*>.refreshHolders() {
//    if (holderCooldown != null) {
//        holderCooldown.getIfPresent(this.uuid) ?: return
//        holderCooldown.put(this.uuid, Unit)
//    }

    this.forceRefreshHolders()
}

/**
 * Forcibly refresh holders, ignoring cooldown.
 */
fun Dispatcher<*>.forceRefreshHolders() {

    refreshFunctions.forEach { it(this) }
    this.updateHolders()
    this.updateEffects()
}

@Deprecated(
    "Use refreshHolders on a dispatcher instead",
    ReplaceWith("toDispatcher().refreshHolders()"),
    DeprecationLevel.ERROR
)
fun Player.refreshHolders() =
    this.toDispatcher().refreshHolders()

private val holderPlaceholderProviders = mutableListOf<(ProvidedHolder, Dispatcher<*>) -> Collection<NamedValue>>()

/**
 * Register a function to generate placeholders for a holder.
 */
fun registerPlaceholderProvider(provider: (ProvidedHolder, Dispatcher<*>) -> Collection<NamedValue>) {
    holderPlaceholderProviders += provider
}

/**
 * Register a function to generate placeholders for a holder for any dispatcher.
 */
inline fun <reified T : Holder> registerHolderPlaceholderProvider(crossinline provider: (T, Dispatcher<*>) -> Collection<NamedValue>) {
    registerPlaceholderProvider { provided, dispatcher ->
        val holder = provided.holder
        if (holder is T) {
            provider(holder, dispatcher)
        } else {
            emptyList()
        }
    }
}

/**
 * Register a function to generate placeholders for a holder for a specific dispatcher.
 */
inline fun <reified T : Holder, reified R> registerSpecificHolderPlaceholderProvider(crossinline provider: (T, R) -> Collection<NamedValue>) {
    registerPlaceholderProvider { provided, dispatcher ->
        val holder = provided.holder
        if (holder is T && dispatcher.isType<R>()) {
            provider(holder, dispatcher.get<R>()!!)
        } else {
            emptyList()
        }
    }
}

/**
 * Generate placeholders for a holder.
 */
fun ProvidedHolder.generatePlaceholders(dispatcher: Dispatcher<*>): List<NamedValue> {
    return holderPlaceholderProviders.flatMap { it(this, dispatcher) }
}

private val previousHolders = mutableMapOf<UUID, Collection<ProvidedHolder>>()

private val holderCache = Caffeine.newBuilder()
    .expireAfterWrite(4, TimeUnit.SECONDS)
    .build<UUID, Collection<ProvidedHolder>>()

/**
 * The holders.
 */
val Dispatcher<*>.holders: Collection<ProvidedHolder>
    get() = holderCache.get(this.uuid) {
        if (this is EntityDispatcher && this.dispatcher !is Player && !plugin.configYml.getBool("refresh.entities.enabled")) {
            return@get emptyList()
        }

        val holders = providers.flatMap { it.provide(this) }

        Bukkit.getPluginManager().callEvent(
            HolderProvideEvent(this, holders)
        )

        val old = previousHolders[this.uuid] ?: emptyList()

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

        previousHolders[this.uuid] = holders

        holders
    }

/**
 * Get holders of a specific type.
 */
inline fun <reified T : Holder> Dispatcher<*>.getHoldersOfType(): Collection<T> {
    return this.holders.mapNotNull { it.holder as? T }
}

@Deprecated(
    "Use a dispatcher instead of a player",
    ReplaceWith("toDispatcher().holders"),
    DeprecationLevel.ERROR
)
val Player.holders: Collection<ProvidedHolder>
    get() = this.toDispatcher().holders

/**
 * Invalidate holder cache to force rescan.
 */
fun Dispatcher<*>.updateHolders() {
    holderCache.invalidate(this.uuid)
}

@Deprecated(
    "Use updateHolders on a dispatcher instead",
    ReplaceWith("toDispatcher().updateHolders()"),
    DeprecationLevel.ERROR
)
fun Player.updateHolders() =
    this.toDispatcher().updateHolders()

internal fun Dispatcher<*>.purgePreviousHolders() {
    previousHolders.remove(this.uuid)
    previousStates.remove(this.uuid)
}

// Effects that were active on previous update
private val previousStates = listMap<UUID, ProvidedEffectBlock>() // Optimisation.

/**
 * Get active effects for a [dispatcher] from holders mapped to the holder
 * that has provided them.
 */
fun Collection<ProvidedHolder>.getProvidedActiveEffects(dispatcher: Dispatcher<*>): List<ProvidedEffectBlock> {
    val blocks = mutableListOf<ProvidedEffectBlock>()

    for (holder in this) {
        if (holder.holder.conditions.areMet(dispatcher, holder)) {
            for (block in holder.getActiveEffects(dispatcher)) {
                blocks += ProvidedEffectBlock(block, holder)
            }
        }
    }

    return blocks.sorted()
}

/**
 * Get active effects for a [dispatcher].
 */
fun ProvidedHolder.getActiveEffects(dispatcher: Dispatcher<*>) =
    this.holder.effects.filter { it.conditions.areMet(dispatcher, this) }.toSet()

@Deprecated(
    "Use getActiveEffects on a dispatcher instead",
    ReplaceWith("getActiveEffects(player.toDispatcher())"),
    DeprecationLevel.ERROR
)
fun ProvidedHolder.getActiveEffects(player: Player) =
    getActiveEffects(player.toDispatcher())

/**
 * Recalculate active effects.
 */
fun Dispatcher<*>.calculateActiveEffects() =
    this.holders.getProvidedActiveEffects(this)

@Deprecated(
    "Use calculateActiveEffects on a dispatcher instead",
    ReplaceWith("toDispatcher().calculateActiveEffects()"),
    DeprecationLevel.ERROR
)
fun Player.calculateActiveEffects() =
    this.toDispatcher().calculateActiveEffects()

/**
 * The active effects.
 */
val Dispatcher<*>.activeEffects: List<EffectBlock>
    get() = previousStates[this.uuid].map { it.effect }

@Deprecated(
    "Use activeEffects on a dispatcher instead",
    ReplaceWith("toDispatcher().activeEffects"),
    DeprecationLevel.ERROR
)
val Player.activeEffects: List<EffectBlock>
    get() = this.toDispatcher().activeEffects

/**
 * The active effects mapped to the holder that provided them.
 */
val Dispatcher<*>.providedActiveEffects: List<ProvidedEffectBlock>
    get() = previousStates[this.uuid]

/**
 * Update the active effects.
 */
fun Dispatcher<*>.updateEffects() {
    val before = this.providedActiveEffects
    val after = this.calculateActiveEffects()

    previousStates[this.uuid] = after.sorted()

    // Permanent effects also have a run order, so we need to sort them.
    val added = (after without before).sorted()
    val removed = (before without after).sorted()
    val toReload = (after without added).sorted()

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

@Deprecated(
    "Use updateEffects on a dispatcher instead",
    ReplaceWith("toDispatcher().updateEffects()"),
    DeprecationLevel.ERROR
)
fun Player.updateEffects() =
    this.toDispatcher().updateEffects()

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
