package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Weighted
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.effects.events.EffectDisableEvent
import com.willfp.libreforge.effects.events.EffectEnableEvent
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

/**
 * A single effect config block.
 */
class ChainElement<T> internal constructor(
    val effect: Effect<T>,
    override val config: Config,
    override val compileData: T,
    override val arguments: EffectArgumentList,
    override val conditions: ConditionList,
    override val mutators: MutatorList,
    override val filters: FilterList,
    override val weight: Double
) : ElementLike(), Compiled<T>, Weighted {
    override val uuid: UUID = UUID.randomUUID()
    override val supportsDelay = effect.supportsDelay

    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectEnableEvent(player, effect, holder))
        }

        effect.enable(player, holder, this, isReload = isReload)
    }

    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectDisableEvent(player, effect, holder))
        }

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

    override fun shouldTrigger(trigger: DispatchedTrigger): Boolean =
        effect.shouldTrigger(trigger, this)
}
