package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.argument.EffectArgumentList
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player
import java.util.UUID

/**
 * A single effect config block.
 */
class ChainElement<T>(
    val effect: Effect<T>,
    val config: Config,
    val compileData: T?,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList
) : ElementLike() {
    override val uuid: UUID = UUID.randomUUID()

    fun enable(player: Player, identifierFactory: IdentifierFactory) {
        effect.enable(player, identifierFactory, this)
    }

    fun disable(player: Player, identifierFactory: IdentifierFactory) {
        effect.disable(player, identifierFactory)
    }

    fun reload(player: Player, identifierFactory: IdentifierFactory) {
        effect.reload(player, identifierFactory, this)
    }

    override fun doTrigger(trigger: DispatchedTrigger) {
        effect.trigger(trigger, this)
    }
}
