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
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.PlayerDispatcher
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
    override val weight: Double,
    forceRunOrder: RunOrder?
) : ElementLike(), Compiled<T>, Weighted {
    override val uuid: UUID = UUID.randomUUID()
    override val supportsDelay = effect.supportsDelay

    val runOrder = forceRunOrder ?: effect.runOrder

    fun enable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectEnableEvent(dispatcher, effect, holder))
        }

        effect.enable(dispatcher, holder, this, isReload = isReload)
    }

    fun disable(
        dispatcher: Dispatcher<*>,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) {
        if (!isReload) {
            Bukkit.getPluginManager().callEvent(EffectDisableEvent(dispatcher, effect, holder))
        }

        effect.disable(dispatcher, holder, isReload = isReload)
    }

    override fun doTrigger(trigger: DispatchedTrigger) =
        effect.trigger(trigger, this)

    override fun shouldTrigger(trigger: DispatchedTrigger): Boolean =
        effect.shouldTrigger(trigger, this)


    @Deprecated(
        "Use enable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("enable(dispatcher, holder, isReload)"),
        DeprecationLevel.ERROR
    )
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = enable(PlayerDispatcher(player), holder, isReload)

    @Deprecated(
        "Use disable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("disable(dispatcher, holder, isReload)"),
        DeprecationLevel.ERROR
    )
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ) = disable(PlayerDispatcher(player), holder, isReload)
}
