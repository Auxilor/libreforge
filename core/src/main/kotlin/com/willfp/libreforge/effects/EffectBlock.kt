package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.BlankHolder.effects
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.PotentiallyTriggerable
import com.willfp.libreforge.triggers.Trigger
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID

/**
 * A compiled group of effects.
 */
class EffectBlock internal constructor(
    override val uuid: UUID,
    override val config: Config,
    val effects: Chain,
    val triggers: Set<Trigger>,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList,
    override val shouldDelegateExecution: Boolean
) : ElementLike(), PotentiallyTriggerable {
    override val supportsDelay = effects.all { it.supportsDelay }

    val weight = effects.weight

    /**
     * Enable the effects.
     */
    fun enable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = effects.forEach { it.enable(dispatcher, holder, isReload = isReload) }

    /**
     * Disable the effects.
     */
    fun disable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = effects.forEach { it.disable(dispatcher, holder, isReload = isReload) }

    @Deprecated(
        "Use enable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("enable(dispatcher, holder, isReload)"),
        DeprecationLevel.ERROR
    )
    @JvmOverloads
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = enable(PlayerDispatcher(player), holder, isReload = isReload)

    @Deprecated(
        "Use disable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("disable(dispatcher, holder, isReload)"),
        DeprecationLevel.ERROR
    )
    @JvmOverloads
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = disable(PlayerDispatcher(player), holder, isReload = isReload)


    fun tryTrigger(trigger: DispatchedTrigger) {
        if (canBeTriggeredBy(trigger.trigger)) {
            trigger(trigger)
        }
    }

    override fun canBeTriggeredBy(trigger: Trigger) =
        trigger in triggers

    override fun doTrigger(trigger: DispatchedTrigger) =
        effects.trigger(trigger)

    override fun equals(other: Any?): Boolean {
        if (other !is EffectBlock) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.uuid)
    }
}
