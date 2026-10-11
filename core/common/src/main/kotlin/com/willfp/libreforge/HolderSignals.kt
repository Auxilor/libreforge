package com.willfp.libreforge

import com.willfp.libreforge.conditions.Condition
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Routes change signals to the providers and conditions that declared them.
 */
internal object HolderSignals {
    private val providersBySignal = ConcurrentHashMap<HolderChange, CopyOnWriteArrayList<HolderProvider>>()

    private val conditionsBySignal = ConcurrentHashMap<HolderChange, CopyOnWriteArraySet<Condition<*>>>()

    private class CustomHandler(
        val owner: Any,
        val action: (Event) -> Unit
    )

    private val customHandlers = ConcurrentHashMap<Class<out Event>, CopyOnWriteArrayList<CustomHandler>>()

    private val listening = ConcurrentHashMap.newKeySet<Class<out Event>>()

    private val registeredConditions: MutableSet<Condition<*>> = Collections.newSetFromMap(IdentityHashMap())

    private object SignalListener : Listener

    fun providersFor(change: HolderChange): List<HolderProvider> =
        providersBySignal[change] ?: emptyList()

    fun conditionsFor(change: HolderChange): Set<Condition<*>> =
        conditionsBySignal[change] ?: emptySet()

    @Synchronized
    fun registerProvider(provider: HolderProvider) {
        for (change in provider.invalidatedBy) {
            if (change is HolderChange.Custom<*>) {
                addCustom(change, provider) { dispatcher, scope -> HolderStates.markProvider(dispatcher, provider, scope) }
            } else {
                providersBySignal.computeIfAbsent(change) { CopyOnWriteArrayList() } += provider
            }
        }
    }

    @Synchronized
    fun registerCondition(condition: Condition<*>) {
        // Registration re-runs on every reload.
        if (!registeredConditions.add(condition)) {
            return
        }

        val signals = condition.invalidatedBy ?: return

        for (change in signals) {
            if (change is HolderChange.Custom<*>) {
                addCustom(change, condition) { dispatcher, scope -> HolderStates.markCondition(dispatcher, condition, scope) }
            } else {
                conditionsBySignal.computeIfAbsent(change) { CopyOnWriteArraySet() } += condition
            }
        }
    }

    /**
     * Stop routing signals to [providers] and to the conditions loaded by [classLoader].
     */
    @Synchronized
    fun unregisterOwnedBy(classLoader: ClassLoader, providers: Collection<HolderProvider>) {
        val removed: MutableSet<Any> = Collections.newSetFromMap(IdentityHashMap())
        removed.addAll(providers)
        registeredConditions.filterTo(removed) { it.javaClass.classLoader === classLoader }
        registeredConditions.removeAll { it in removed }

        for (list in providersBySignal.values) {
            list.removeAll { it in removed }
        }

        for (set in conditionsBySignal.values) {
            set.removeAll { it in removed }
        }

        for (handlers in customHandlers.values) {
            handlers.removeAll { it.owner in removed }
        }
    }

    private fun addCustom(change: HolderChange.Custom<*>, owner: Any, mark: (Dispatcher<*>, SignalScope?) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val dispatcherOf = change.dispatcherOf as (Event) -> Dispatcher<*>?

        @Suppress("UNCHECKED_CAST")
        val scopeOf = change.scopeOf as ((Event) -> SignalScope?)?

        customHandlers.computeIfAbsent(change.event) { CopyOnWriteArrayList() } += CustomHandler(owner) { event ->
            dispatcherOf(event)?.let { mark(it, scopeOf?.invoke(event)) }
        }

        val eventClass = change.event
        plugin.runWhenEnabled {
            if (listening.add(eventClass)) {
                Bukkit.getPluginManager().registerEvent(
                    eventClass,
                    SignalListener,
                    EventPriority.MONITOR,
                    EventExecutor { _, event ->
                        if (eventClass.isInstance(event)) {
                            customHandlers[eventClass]?.toList()?.forEach { it.action(event) }
                        }
                    },
                    plugin,
                    true
                )
            }
        }
    }
}
