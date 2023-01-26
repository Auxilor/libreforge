package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player

/**
 * A single effect config block.
 */
class EffectBlock<T>(
    val effect: Effect<T>,
    val config: Config,
    val compileData: T?,
    val mutators: MutatorList,
    val filters: FilterList
) {
    fun enable(player: Player, identifierFactory: IdentifierFactory) {
        effect.enable(player, identifierFactory, this)
    }

    fun disable(player: Player, identifierFactory: IdentifierFactory) {
        effect.disable(player, identifierFactory)
    }

    fun reload(player: Player, identifierFactory: IdentifierFactory) {
        effect.reload(player, identifierFactory, this)
    }

    fun trigger(player: Player, data: TriggerData) {
        if (!filters.filter(data)) {
            return
        }

        effect.trigger(player, mutators.mutate(data), this)
    }
}
