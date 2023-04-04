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
import java.util.*

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

    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        effect.enable(player, holder, this, isReload = isReload)
    }

    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        effect.disable(player, holder, isReload = isReload)
    }

    @Deprecated(
        "Use enable(player, holder) instead",
        ReplaceWith("enable(player, holder)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun enable(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        effect.enable(player, holder, this)
    }

    @Deprecated(
        "Use disable(player, holder) instead",
        ReplaceWith("disable(player, holder)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun disable(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        effect.disable(player, holder)
    }

    @Deprecated(
        "Reloading is now handled by effect blocks",
        ReplaceWith("effectBlock.reload(player, holder)"),
        DeprecationLevel.ERROR
    )
    @Suppress("UNUSED_PARAMETER")
    fun reload(player: Player, holder: ProvidedHolder, identifierFactory: IdentifierFactory) {
        // Do nothing.
    }

    override fun doTrigger(trigger: DispatchedTrigger) =
        effect.trigger(trigger, this)
}
