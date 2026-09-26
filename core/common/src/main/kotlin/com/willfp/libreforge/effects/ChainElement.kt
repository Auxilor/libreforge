package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Weighted
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.effects.events.EffectDisableEvent
import com.willfp.libreforge.effects.events.EffectEnableEvent
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Bukkit
import java.util.UUID

/**
 * A single effect config block.
 */
class ChainElement<T> internal constructor(
    val effect: Effect<T>,
    override val config: Config,
    private val elementConfig: Config,
    override val compileData: T,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList,
    override val weight: Double,
    forceRunOrder: RunOrder?
) : ElementLike(), Compiled<T>, Weighted {
    override val uuid: UUID = UUID.randomUUID()
    override val supportsDelay = effect.supportsDelay

    val runOrder = forceRunOrder ?: effect.runOrder

    fun getWeight(trigger: DispatchedTrigger): Double {
        if (!elementConfig.has("weight")) {
            return weight
        }

        elementConfig.addInjectablePlaceholder(trigger.placeholders)

        return runCatching {
            elementConfig.getDoubleFromExpression("weight", trigger.data)
        }.getOrDefault(weight)
    }

    fun enable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        blockIndex: Int = 0,
        elementIndex: Int = 0,
        occurrence: Int = 0,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectEnableEvent(dispatcher, effect, holder))
        }

        effect.enable(dispatcher, holder, this, blockIndex, elementIndex, occurrence, isReload = isReload)
    }

    fun disable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        blockIndex: Int = 0,
        elementIndex: Int = 0,
        occurrence: Int = 0,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectDisableEvent(dispatcher, effect, holder))
        }

        effect.disable(dispatcher, holder, blockIndex, elementIndex, occurrence, isReload = isReload)
    }

    internal fun enableActive(dispatcher: Dispatcher<*>, holder: ProvidedHolder, identifiers: Identifiers) {
        Bukkit.getPluginManager().callEvent(EffectEnableEvent(dispatcher, effect, holder))
        effect.enableWith(dispatcher, holder, this, identifiers)
    }

    internal fun disableActive(dispatcher: Dispatcher<*>, holder: ProvidedHolder, identifiers: Identifiers) {
        Bukkit.getPluginManager().callEvent(EffectDisableEvent(dispatcher, effect, holder))
        effect.disableWith(dispatcher, holder, identifiers)
    }

    internal fun reloadActive(
        dispatcher: Dispatcher<*>,
        previous: ProvidedHolder,
        current: ProvidedHolder,
        identifiers: Identifiers
    ): Boolean = effect.reloadWith(dispatcher, previous, current, this, identifiers)

    internal fun repairActive(
        dispatcher: Dispatcher<*>,
        previous: ProvidedHolder,
        current: ProvidedHolder,
        identifiers: Identifiers
    ) = effect.repairWith(dispatcher, previous, current, this, identifiers)

    override fun doTrigger(trigger: DispatchedTrigger) =
        effect.trigger(trigger, this)

    override fun shouldTrigger(trigger: DispatchedTrigger): Boolean =
        effect.shouldTrigger(trigger, this)
}
