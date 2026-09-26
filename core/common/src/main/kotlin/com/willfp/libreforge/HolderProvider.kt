package com.willfp.libreforge

import com.willfp.libreforge.effects.EffectBlock
import com.willfp.libreforge.slot.ItemHolderFinder
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import java.util.IdentityHashMap


/**
 * Provides the holders that are held by a player.
 */
interface HolderProvider {
    /**
     * Provide the holders.
     *
     * Holders must be returned in a stable order across calls: duplicates of one holder id are
     * identified by their position among the holders with that id, so a provider that iterates an
     * unordered collection can make an unchanged holder look moved.
     */
    fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder>

    /**
     * Stable id, unique among registered providers (duplicates are suffixed with `#<n>`).
     */
    val id: String
        get() = this::class.java.name

    /**
     * The maximum number of ticks between re-asks for a [dispatcher] without invalidation,
     * or null to only re-ask when invalidated.
     */
    fun maxAge(dispatcher: Dispatcher<*>): Int? = HolderPolling.defaultMaxAge(dispatcher)

    /**
     * The change signals that invalidate this provider.
     */
    val invalidatedBy: Set<HolderChange>
        get() = HolderChange.BUILT_IN
}

/**
 * A signal that the holders of a dispatcher may have changed.
 */
sealed interface HolderChange {
    /**
     * Any inventory, equipment, held item, pickup, drop or use change.
     */
    data object Items : HolderChange

    /**
     * The player respawned.
     */
    data object Respawn : HolderChange

    /**
     * The player changed world.
     */
    data object WorldChange : HolderChange

    /**
     * Any Bukkit event. [dispatcherOf] names the one dispatcher to refresh; null ignores the event.
     */
    class Custom<E : Event>(
        val event: Class<E>,
        val dispatcherOf: (E) -> Dispatcher<*>?
    ) : HolderChange

    companion object {
        /**
         * The signals wired by libreforge.
         */
        val BUILT_IN: Set<HolderChange> = setOf(Items, Respawn, WorldChange)

        /**
         * Create a [Custom] signal for an event type.
         */
        inline fun <reified E : Event> custom(noinline dispatcherOf: (E) -> Dispatcher<*>?) =
            Custom(E::class.java, dispatcherOf)
    }
}

/**
 * A typed holder provider.
 */
interface TypedHolderProvider<T : Holder> : HolderProvider {
    override fun provide(dispatcher: Dispatcher<*>): Collection<TypedProvidedHolder<T>>
}

@Deprecated(
    "HolderProvideEvent now only fires when the holder set changes, not on every refresh. " +
    "Use HolderEnableEvent and HolderDisableEvent to react to holder additions and removals. " +
    "This event will be removed in a future version.",
    level = DeprecationLevel.WARNING
)
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

private val providerIds = IdentityHashMap<HolderProvider, String>()

private val providerIdCounts = mutableMapOf<String, Int>()

/**
 * The registered providers, in registration order.
 */
internal val registeredHolderProviders: List<HolderProvider>
    get() = providers

/**
 * The unique id of a registered [provider].
 */
internal fun registeredProviderId(provider: HolderProvider): String =
    providerIds[provider] ?: provider.id

/**
 * Register a new holder provider.
 */
fun registerHolderProvider(provider: HolderProvider): Boolean {
    if (providerIds.containsKey(provider)) {
        return false
    }

    val baseId = provider.id
    val count = providerIdCounts.getOrDefault(baseId, 0)
    providerIdCounts[baseId] = count + 1
    providerIds[provider] = if (count == 0) baseId else "$baseId#$count"

    providers.add(provider)
    HolderSignals.registerProvider(provider)
    HolderStates.onProviderRegistered(provider)
    return true
}

/**
 * Remove holder providers, used when the plugin that registered them is disabled.
 */
internal fun unregisterHolderProviders(filter: (HolderProvider) -> Boolean): List<HolderProvider> {
    val removed = providers.filter(filter)
    providers.removeAll(removed)
    removed.forEach { providerIds.remove(it) }
    return removed
}

/**
 * The class that owns a provider, used to find the plugin that registered it.
 */
internal val HolderProvider.ownerClass: Class<*>
    get() = when (this) {
        is GenericHolderProvider -> function.javaClass
        is ItemHolderFinder<*>.ItemHolderFinderProvider -> finderClass
        else -> this.javaClass
    }

internal class GenericHolderProvider(
    val function: (Dispatcher<*>) -> Collection<ProvidedHolder>,
    private val explicitId: String?,
    private val maxAgeFunction: ((Dispatcher<*>) -> Int?)?,
    override val invalidatedBy: Set<HolderChange>
) : HolderProvider {
    override val id: String
        get() = explicitId ?: function.javaClass.name

    override fun maxAge(dispatcher: Dispatcher<*>): Int? =
        if (maxAgeFunction != null) maxAgeFunction(dispatcher) else HolderPolling.defaultMaxAge(dispatcher)

    override fun provide(dispatcher: Dispatcher<*>) = function(dispatcher)
}

/**
 * Register a new holder provider for all possible dispatchers.
 */
fun registerGenericHolderProvider(provider: (Dispatcher<*>) -> Collection<ProvidedHolder>) =
    registerGenericHolderProvider(id = null, provider = provider)

/**
 * Register a new holder provider for all possible dispatchers, with an [id], a [maxAge] (see
 * [HolderProvider.maxAge], null to use the default) and the signals it is [invalidatedBy].
 */
fun registerGenericHolderProvider(
    id: String? = null,
    maxAge: ((Dispatcher<*>) -> Int?)? = null,
    invalidatedBy: Set<HolderChange> = HolderChange.BUILT_IN,
    provider: (Dispatcher<*>) -> Collection<ProvidedHolder>
) = registerHolderProvider(GenericHolderProvider(provider, id, maxAge, invalidatedBy))

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

/**
 * Register a new holder provider for a specific type of dispatcher, with an [id], a [maxAge] (see
 * [HolderProvider.maxAge], null to use the default) and the signals it is [invalidatedBy].
 */
inline fun <reified T> registerSpecificHolderProvider(
    id: String? = null,
    noinline maxAge: ((Dispatcher<*>) -> Int?)? = null,
    invalidatedBy: Set<HolderChange> = HolderChange.BUILT_IN,
    crossinline provider: (T) -> Collection<ProvidedHolder>
): Boolean {
    val explicitId = id
    val maxAgeFunction = maxAge
    val signals = invalidatedBy

    return registerHolderProvider(object : HolderProvider {
        override val id: String
            get() = explicitId ?: this::class.java.name

        override val invalidatedBy: Set<HolderChange>
            get() = signals

        override fun maxAge(dispatcher: Dispatcher<*>): Int? =
            if (maxAgeFunction != null) maxAgeFunction(dispatcher) else HolderPolling.defaultMaxAge(dispatcher)

        override fun provide(dispatcher: Dispatcher<*>): Collection<ProvidedHolder> {
            return if (dispatcher.isType<T>()) {
                provider(dispatcher.get<T>()!!)
            } else {
                emptyList()
            }
        }
    })
}

fun registerSlotHolderFinderAsProvider(finder: ItemHolderFinder<*>) =
    registerHolderProvider(finder.toHolderProvider())

private val refreshFunctions = mutableListOf<(Dispatcher<*>) -> Unit>()

/**
 * Register a function to be called before a dispatcher's providers are re-asked.
 */
fun registerRefreshFunction(function: (Dispatcher<*>) -> Unit) {
    refreshFunctions += function
}

/**
 * Register a function to be called before a dispatcher's providers are re-asked, for a specific dispatcher.
 */
inline fun <reified T> registerSpecificRefreshFunction(crossinline function: (T) -> Unit) {
    registerRefreshFunction {
        it.get<T>()?.let { t ->
            function(t)
        }
    }
}

internal fun Dispatcher<*>.runRefreshFunctions() {
    refreshFunctions.forEach { it(this) }
}

/**
 * Remove the refresh functions and placeholder providers whose code was loaded by [classLoader].
 */
internal fun unregisterHolderFunctions(classLoader: ClassLoader) {
    refreshFunctions.removeAll { it.javaClass.classLoader === classLoader }
    holderPlaceholderProviders.removeAll { it.javaClass.classLoader === classLoader }
}

/**
 * Invalidate one [provider] on this dispatcher, for changes with no event. Callable from any thread.
 */
fun Dispatcher<*>.invalidate(provider: HolderProvider) =
    HolderStates.markProvider(this, provider)

/**
 * Invalidate this provider on every tracked dispatcher. Callable from any thread.
 */
fun HolderProvider.invalidateEverywhere() =
    HolderStates.markProviderEverywhere(this)

/**
 * Invalidate every provider for this dispatcher; applied in the next tick. Callable from any thread.
 */
fun Dispatcher<*>.refreshHolders() =
    HolderStates.markAllProviders(this)

/**
 * Invalidate every provider for this dispatcher, ignoring `refresh.cooldown`; applied in the next
 * tick. Callable from any thread.
 */
fun Dispatcher<*>.forceRefreshHolders() =
    HolderStates.forceMarkAllProviders(this)

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
    return buildList {
        for (provider in holderPlaceholderProviders) {
            addAll(provider(this@generatePlaceholders, dispatcher))
        }
    }
}

/**
 * The holders.
 */
val Dispatcher<*>.holders: Collection<ProvidedHolder>
    get() = HolderStates.holders(this)

/**
 * Get holders of a specific type.
 */
inline fun <reified T : Holder> Dispatcher<*>.getHoldersOfType(): Collection<T> {
    return this.holders.mapNotNull { it.holder as? T }
}

/**
 * Invalidate every provider for this dispatcher; applied in the next tick. Callable from any thread.
 */
fun Dispatcher<*>.updateHolders() =
    HolderStates.markAllProviders(this)

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

/**
 * Recalculate active effects.
 */
fun Dispatcher<*>.calculateActiveEffects() =
    this.holders.getProvidedActiveEffects(this)

/**
 * The active effects.
 */
val Dispatcher<*>.activeEffects: List<EffectBlock>
    get() = HolderStates.providedActiveEffects(this).map { it.effect }

/**
 * The active effects mapped to the holder that provided them.
 */
val Dispatcher<*>.providedActiveEffects: List<ProvidedEffectBlock>
    get() = HolderStates.providedActiveEffects(this)

/**
 * Re-evaluate the conditions of every holder; applied in the next tick. Callable from any thread.
 */
fun Dispatcher<*>.updateEffects() =
    HolderStates.markConditionsAll(this)

/**
 * Removes all elements from the given [other] list that are contained in this list.
 *
 * Elements are only removed as many times as they are present.
 */
inline infix fun <reified T> Collection<T>.without(other: Collection<T>): List<T> {
    val counts = HashMap<T, Int>(other.size)
    for (element in other) {
        counts[element] = (counts[element] ?: 0) + 1
    }
    return filter { element ->
        val count = counts[element]
        if (count != null && count > 0) {
            counts[element] = count - 1
            false
        } else {
            true
        }
    }
}
