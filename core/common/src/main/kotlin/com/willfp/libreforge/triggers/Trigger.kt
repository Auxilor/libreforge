package com.willfp.libreforge.triggers

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedEffectBlock
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.counters.bind.BoundCounters
import com.willfp.libreforge.counters.bind.BoundCounters.bindings
import com.willfp.libreforge.generatePlaceholders
import com.willfp.libreforge.getProvidedActiveEffects
import com.willfp.libreforge.plugin
import com.willfp.libreforge.providedActiveEffects
import com.willfp.libreforge.triggers.event.TriggerDispatchEvent
import com.willfp.libreforge.triggers.impl.TriggerAltClick
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Listener

abstract class Trigger(
    override val id: String
) : Listener, KRegistrable {
    /**
     * The TriggerData parameters that are sent.
     */
    abstract val parameters: Set<TriggerParameter>

    /**
     * Human-readable description of when this trigger fires, shown on the wiki.
     */
    open val description: String = ""

    /**
     * Category tags for grouping and filtering on the wiki, e.g. "combat", "movement".
     */
    open val categories: Set<String> = emptySet()

    /**
     * Additional notes, warnings, or constraints shown on the wiki.
     */
    open val additionalInfo: List<String> = emptyList()

    /**
     * Per-parameter descriptions giving context specific to this trigger.
     * Describes what each TriggerParameter value represents when this trigger fires,
     * e.g. what VICTIM or VALUE actually contains for this specific event.
     */
    open val parameterDescriptions: Map<TriggerParameter, String> = emptyMap()

    /**
     * Whether this trigger is enabled.
     */
    open var isEnabled: Boolean = false
        protected set

    /**
     * If the listener is registered.
     */
    private var isListenerRegistered = false

    /**
     * The cached hashcode.
     */
    private val hashCode by lazy {
        id.hashCode()
    }

    /**
     * Enable the trigger.
     */
    fun enable() {
        isEnabled = true

        if (!this.isListenerRegistered) {
            this.isListenerRegistered = true
            plugin.runWhenEnabled {
                plugin.eventManager.unregisterListener(this)
                plugin.eventManager.registerListener(this)
                postRegister()
            }
        }
    }


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
    private fun dispatchOnEffects(
        dispatcher: Dispatcher<*>,
        data: TriggerData,
        effects: List<ProvidedEffectBlock>
    ) {
        // Do this first to filter disabled triggers
        val dispatch = plugin.dispatchedTriggerFactory.create(dispatcher, this, data) ?: return

        // Filter out effects that can't be triggered by this trigger
        val triggerableEffects = mutableListOf<ProvidedEffectBlock>()

        for (block in effects) {
            if (block.effect.canBeTriggeredBy(this)) {
                triggerableEffects += block
            }
        }

        // Prevent dispatching useless triggers
        if (triggerableEffects.isEmpty() && !BoundCounters.anyCanBeTriggeredBy(this)) {
            return
        }

        // Only dispatch placeholders after we know we're going to dispatch
        dispatch.generatePlaceholders()

        val dispatchEvent = TriggerDispatchEvent(dispatcher, dispatch)
        Bukkit.getPluginManager().callEvent(dispatchEvent)

        if (dispatchEvent.isCancelled) {
            return
        }

        // Only calculate placeholders once per holder
        val holderDispatches = mutableMapOf<ProvidedHolder, DispatchedTrigger>()

        for ((_, holder) in triggerableEffects) {
            val withHolder = dispatch.data.copy().apply {
                this.holder = holder
            }

            val dispatchWithHolder = DispatchedTrigger(dispatcher, this, withHolder).inheritPlaceholders(dispatch)

            dispatchWithHolder.addPlaceholders(holder.generatePlaceholders(dispatcher))

            holderDispatches[holder] = dispatchWithHolder
        }

        // Finally, trigger effects
        for ((block, holder) in triggerableEffects) {
            // Fixes cancel_event not working
            if (data.event is Cancellable && data.event.isCancelled) {
                // alt_click triggers are special and should be allowed to trigger even if the event is cancelled
                // Otherwise, clicking the air will not trigger the alt_click trigger
                if (this !is TriggerAltClick) {
                    return
                }
            }

            val dispatchWithHolder = holderDispatches[holder] ?: continue

            block.tryTrigger(dispatchWithHolder)
        }

        // Probably a better way to work with counters, but this works for now.
        for (counter in BoundCounters.values()) {
            counter.bindings.forEach { it.accept(dispatch) }
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
        return hashCode
    }
}
