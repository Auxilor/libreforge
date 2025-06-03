package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.Compiled
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.Weighted
import com.willfp.libreforge.conditions.ConditionList
import com.willfp.libreforge.effects.arguments.EffectArgumentList
import com.willfp.libreforge.effects.events.EffectDisableEvent
import com.willfp.libreforge.effects.events.EffectEnableEvent
import com.willfp.libreforge.filters.FilterList
import com.willfp.libreforge.mutators.MutatorList
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.DispatchedTrigger
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.random.Random

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
    val weightExpression:String,
    forceRunOrder: RunOrder?
) : ElementLike(), Compiled<T>, Weighted {
    override val uuid: UUID = UUID.randomUUID()
    override val supportsDelay = effect.supportsDelay
    var tempPlayer :Player? = null
    val runOrder = forceRunOrder ?: effect.runOrder

     override fun calcWeight() : Double{
        if(weightExpression.isEmpty())return 0.0
        var expressionCalculated:String
        val weight:Double
        if(tempPlayer!=null){
            expressionCalculated = PlaceholderAPI.setPlaceholders(tempPlayer,weightExpression)
            weight = NumberUtils.evaluateExpression(expressionCalculated,placeholderContext(player = tempPlayer))
        }else{
            expressionCalculated = weightExpression
            weight = NumberUtils.evaluateExpression(expressionCalculated);
        }
        return weight
    }
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

    override fun doTrigger(trigger: DispatchedTrigger):Boolean {
        val player:Player? = trigger.dispatcher.dispatcher as? Player
        if(player!=null){
            tempPlayer = player
        }
        this.config.set("calculated_weight",calcWeight())
        return effect.trigger(trigger, this)
    }


    override fun shouldTrigger(trigger: DispatchedTrigger): Boolean =
        effect.shouldTrigger(trigger, this)


    @Deprecated(
        "Use enable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("enable(player.toDispatcher(), holder, isReload)"),
        DeprecationLevel.ERROR
    )
    fun enable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ): Unit {
        tempPlayer = player
        return enable(player.toDispatcher(), holder, isReload)
    }

    @Deprecated(
        "Use disable(Dispatcher<*>, ProvidedHolder, Boolean)",
        ReplaceWith("disable(player.toDispatcher(), holder, isReload)"),
        DeprecationLevel.ERROR
    )
    fun disable(
        player: Player,
        holder: ProvidedHolder,
        isReload: Boolean = false
    ): Unit = disable(player.toDispatcher(), holder, isReload)

    override fun toString(): String {
        return effect.id+ config.toString();
    }
}
