package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import java.util.Objects
import java.util.UUID

/**
 * A compiled group of effects.
 */
class EffectGroup(
    val uuid: UUID,
    val config: Config,
    val effects: Chain,
    val conditions: ConditionList,
    val triggers: Collection<Trigger>,
    val mutators: MutatorList,
    val filters: FilterList
) {
    private val identifierFactory = IdentifierFactory(uuid)

    fun enable(player: Player) =
        effects.forEach { it.enable(player, identifierFactory) }

    fun disable(player: Player) =
        effects.forEach { it.disable(player, identifierFactory) }

    fun reload(player: Player) =
        effects.forEach { it.reload(player, identifierFactory) }

    fun trigger(player: Player, trigger: Trigger, data: TriggerData) {
        if (trigger !in triggers) {
            return
        }

        val mutated = mutators.mutate(data)

        if (!filters.filter(mutated)) {
            return
        }

        effects.trigger(player, mutated)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is EffectGroup) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hashCode(this.uuid)
    }
}
