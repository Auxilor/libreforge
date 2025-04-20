package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import java.util.Objects
import java.util.UUID

/**
 * A rich chain, used in nested chains.
 */
class RichChain internal constructor(
    override val uuid: UUID,
    override val config: Config,
    val effects: Chain,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList
) : ElementLike() {
    override val shouldDelegateExecution = false

    override val supportsDelay = effects.all { it.supportsDelay }

    override fun doTrigger(trigger: DispatchedTrigger) =
        effects.trigger(trigger)

    override fun equals(other: Any?): Boolean {
        if (other !is RichChain) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.uuid)
    }
}
