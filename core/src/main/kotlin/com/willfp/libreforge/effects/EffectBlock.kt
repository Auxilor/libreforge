package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.Trigger
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID

/**
 * A compiled group of effects.
 */
class EffectBlock(
    override val uuid: UUID,
    override val config: Config,
    val effects: Chain,
    val triggers: Collection<Trigger>,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList
) : ElementLike() {
    private val identifierFactory = IdentifierFactory(uuid)
    override val supportsDelay = effects.all { it.supportsDelay }

    fun enable(player: Player) =
        effects.forEach { it.enable(player, identifierFactory) }

    fun disable(player: Player) =
        effects.forEach { it.disable(player, identifierFactory) }

    fun reload(player: Player) =
        effects.forEach { it.reload(player, identifierFactory) }

    fun tryTrigger(trigger: DispatchedTrigger) {
        if (trigger.trigger !in triggers) {
            return
        }

        trigger(trigger)
    }

    override fun doTrigger(trigger: DispatchedTrigger) {
        effects.trigger(trigger)
    }

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
