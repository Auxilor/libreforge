package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ProvidedHolder
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
class EffectBlock internal constructor(
    override val uuid: UUID,
    override val config: Config,
    val effects: Chain,
    val triggers: Collection<Trigger>,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList
) : ElementLike() {
    override val supportsDelay = effects.all { it.supportsDelay }

    @JvmOverloads
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = effects.forEach { it.enable(player, holder, isReload = isReload) }

    @JvmOverloads
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = effects.forEach { it.disable(player, holder, isReload = isReload) }

    fun tryTrigger(trigger: DispatchedTrigger) {
        if (trigger.trigger !in triggers) {
            return
        }

        trigger(trigger)
    }

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
