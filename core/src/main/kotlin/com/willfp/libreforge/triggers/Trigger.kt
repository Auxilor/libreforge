package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedEffectBlock
import com.willfp.libreforge.ProvidedEffectBlocks
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.counters.bind.BoundCounters
import com.willfp.libreforge.counters.bind.BoundCounters.bindings
import com.willfp.libreforge.generatePlaceholders
import com.willfp.libreforge.getProvidedActiveEffects
import com.willfp.libreforge.plugin
import com.willfp.libreforge.providedActiveEffects
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class Trigger(
    override val id: String
) : Listener, KRegistrable {
    /**
     * The TriggerData parameters that are sent.
     */
    abstract val parameters: Set<TriggerParameter>

    /**
     * Whether this trigger is enabled.
     */
    open var isEnabled: Boolean = false
        protected set

    /**
     * Enable the trigger.
     */
    fun enable() {
        isEnabled = true
    }

    @Deprecated(
        "Use dispatch(dispatcher, data, forceHolders) instead",
        ReplaceWith("dispatch(player.toDispatcher(), data, forceHolders)"),
        DeprecationLevel.ERROR
    )
    fun dispatch(
        player: Player,
        data: TriggerData,
        forceHolders: Collection<ProvidedHolder>? = null
    ) = dispatch(player.toDispatcher(), data, forceHolders)

    /**
     * Dispatch the trigger.
     */
    fun dispatch(
        dispatcher: Dispatcher<*>,
        data: TriggerData,
        forceHolders: Collection<ProvidedHolder>? = null
    ) = dispatchOnEffects(
        dispatcher,
        data,
        forceHolders?.getProvidedActiveEffects(dispatcher) ?: dispatcher.providedActiveEffects
    )

    /**
     * Dispatch the trigger on a collection of [ProvidedEffectBlock]s.
     */
    fun dispatchOnEffects(
        dispatcher: Dispatcher<*>,
        data: TriggerData,
        effects: Collection<ProvidedEffectBlocks>
    ) {
        val dispatch = plugin.dispatchedTriggerFactory.create(dispatcher, this, data) ?: return

        // Prevent dispatching useless triggers
        val potentialDestinations = effects.flatMap { it.effects } + BoundCounters.values()
        if (potentialDestinations.none { it.canBeTriggeredBy(this) }) {
            return
        }

        dispatch.generatePlaceholders()

        val dispatchEvent = TriggerDispatchEvent(dispatcher, dispatch)
        Bukkit.getPluginManager().callEvent(dispatchEvent)

        if (dispatchEvent.isCancelled) {
            return
        }

        for ((holder, blocks) in effects) {
            // Check again here to avoid generating placeholders for nothing
            if (blocks.none { it.canBeTriggeredBy(this) }) {
                continue
            }

            val withHolder = dispatch.data.copy(holder = holder)
            val dispatchWithHolder = DispatchedTrigger(dispatcher, this, withHolder).inheritPlaceholders(dispatch)

            for (placeholder in holder.generatePlaceholders(dispatcher)) {
                dispatchWithHolder.addPlaceholder(placeholder)
            }

            for (block in blocks) {
                block.tryTrigger(dispatchWithHolder)
            }
        }

        // Probably a better way to work with counters, but this works for now.
        for (counter in BoundCounters.values()) {
            counter.bindings.forEach { it.accept(dispatch) }
        }
    }


    final override fun onRegister() {
        plugin.runWhenEnabled {
            plugin.eventManager.unregisterListener(this)
            plugin.eventManager.registerListener(this)
            postRegister()
        }
    }

    open fun postRegister() {
        // Override when needed.
    }

    override fun getID() = id

    override fun equals(other: Any?): Boolean {
        return other is Trigger && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
