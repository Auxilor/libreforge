package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
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
    override val config: Config,
    override val compileData: T,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList
) : ElementLike(), Compiled<T> {
    override val uuid: UUID = UUID.randomUUID()
    override val supportsDelay = effect.supportsDelay

    fun enable(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        effect.enable(player, identifierFactory, holder, this)
    }

    fun disable(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        effect.disable(player, identifierFactory, holder)
    }

    fun reload(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        effect.reload(player, identifierFactory, holder, this)
    }

    override fun doTrigger(trigger: DispatchedTrigger) =
        effect.trigger(trigger, this)
}
