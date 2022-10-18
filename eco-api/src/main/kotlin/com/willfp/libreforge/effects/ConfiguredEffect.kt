package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.BlankHolder
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.events.EffectPreActivateEvent
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.triggers.ConfiguredDataMutator
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.mutate
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.max

data class ConfiguredEffect internal constructor(
    val effect: Effect,
    val args: Config,
    val filter: Config,
    val triggers: Collection<Trigger>,
    val uuid: UUID,
    val conditions: Collection<ConfiguredCondition>,
    val mutators: Collection<ConfiguredDataMutator>,
    val compileData: CompileData?,
    private val repeatData: RepeatData
) {
    private val generator = IDGenerator(uuid)
    private val effectStack = mutableMapOf<UUID, Int>()

    fun enableFor(player: Player) {
        val offset = (effectStack[player.uniqueId] ?: 0) + 1
        effectStack[player.uniqueId] = offset
        val identifiers = generator.makeIdentifiers(offset)
        effect.handleEnable(player, args, identifiers)
        effect.handleEnable(player, args, identifiers, compileData)
    }

    fun disableFor(player: Player) {
        val offset = effectStack[player.uniqueId] ?: 0
        effect.handleDisable(player, generator.makeIdentifiers(offset))
        effectStack[player.uniqueId] = max(offset - 1, 0)
    }

    internal operator fun invoke(
        rawInvocation: InvocationData,
        acceptAllTriggers: Boolean = false,
        namedArguments: Iterable<NamedArgument> = emptyList(),
        useTriggerPlayerForConditions: Boolean = false
    ) {
        if (!acceptAllTriggers) {
            if (!triggers.contains(rawInvocation.trigger)) {
                return
            }
        }

        var invocation = rawInvocation.copy(compileData = compileData)

        val allArguments = namedArguments + rawInvocation.createPlaceholders()

        args.addInjectablePlaceholder(allArguments.flatMap { it.placeholders })
        mutators.forEach { it.config.addInjectablePlaceholder(allArguments.flatMap { a -> a.placeholders }) }
        conditions.forEach { it.config.addInjectablePlaceholder(allArguments.flatMap { a -> a.placeholders }) }
        filter.addInjectablePlaceholder(allArguments.flatMap { a -> a.placeholders })

        if (args.getBool("self_as_victim")) {
            invocation = invocation.copy(
                data = invocation.data.copy(victim = invocation.data.player)
            )
        }

        repeatData.update(args.getSubsection("repeat"), invocation.player)

        invocation = invocation.copy(
            data = mutators.mutate(invocation.data)
        )

        val unmetConditions = mutableListOf<ConfiguredCondition>()
        for (condition in conditions) {
            if (useTriggerPlayerForConditions) {
                val player = invocation.data.player
                if (player != null) {
                    if (!condition.isMet(player)) {
                        unmetConditions.add(condition)
                    }
                }
            } else {
                if (!condition.isMet(invocation.player)) {
                    unmetConditions.add(condition)
                }
            }
        }

        val conditionsAreMet = unmetConditions.isEmpty()

        val (player, data, holder, _) = invocation

        if (data.player != null && data.victim != null) {
            if (!args.getBool("disable_antigrief_check")) {
                if (!AntigriefManager.canInjure(data.player, data.victim)) {
                    return
                }
            }
        }

        if (args.getBool("filters_before_mutation")) {
            if (!Filters.passes(rawInvocation.data, filter)) {
                return
            }

        } else {
            if (!Filters.passes(data, filter)) {
                return
            }
        }

        val metArguments = mutableListOf<EffectArgument>()
        val notMetArguments = mutableListOf<EffectArgument>()
        val presentArguments = mutableListOf<EffectArgument>()

        for (argument in Effects.effectArguments()) {
            if (argument.isPresent(args)) {
                presentArguments += argument

                val met = argument.isMet(this, invocation, args)
                if (met) {
                    metArguments += argument
                } else {
                    notMetArguments += argument
                }
            }
        }

        if (notMetArguments.isNotEmpty()) {
            return
        }

        val preActivateEvent = EffectPreActivateEvent(player, holder, effect, args)
        LibReforgePlugin.instance.server.pluginManager.callEvent(preActivateEvent)

        // Not met effects should only run if the effect was already going to run
        if (!conditionsAreMet) {
            for (condition in unmetConditions) {
                for (notMetEffect in condition.notMetEffects) {
                    notMetEffect(
                        invocation,
                        acceptAllTriggers = true,
                        namedArguments = namedArguments
                    )
                }
            }

            return
        }

        if (preActivateEvent.isCancelled) {
            return
        }

        for (argument in notMetArguments) {
            argument.ifNotMet(this, invocation, args)
        }

        for (argument in metArguments) {
            argument.ifMet(this, invocation, args)
        }

        for (argument in presentArguments) {
            argument.always(this, invocation, args)
        }

        val activateEvent = EffectActivateEvent(player, holder, effect, args)
        LibReforgePlugin.instance.server.pluginManager.callEvent(activateEvent)

        if (activateEvent.isCancelled) {
            return
        }

        for (i in 0 until repeatData.times) {
            /*
            Can't use the destructured objects as they haven't been affected by subsequent mutations in repeats.
             */
            val delay = if (args.has("delay")) {
                val found = args.getIntFromExpression("delay", invocation.player)

                if (effect.noDelay || found < 0) 0 else found
            } else 0

            if (delay > 0) {
                LibReforgePlugin.instance.scheduler.runLater(delay.toLong()) {
                    effect.handle(invocation.data, args)
                    effect.handle(invocation, args)
                }
            } else {
                effect.handle(invocation.data, args)
                effect.handle(invocation, args)
            }

            repeatData.count += repeatData.increment

            invocation = invocation.copy(data = mutators.mutate(rawInvocation.copy(compileData = compileData).data))
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ConfiguredEffect) {
            return false
        }

        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}

interface CompileData {
    val data: Any
}

internal data class RepeatData(
    var times: Int,
    var start: Double,
    var increment: Double,
    var count: Double
) {
    fun update(config: Config, player: Player) {
        times = config.getIntFromExpression("times", player)
        start = config.getDoubleFromExpression("start", player)
        increment = config.getDoubleFromExpression("increment", player)
        count = config.getDoubleFromExpression("start", player)

        if (times < 1) times = 1
    }
}

private class EffectSet(effects: Collection<ConfiguredEffect>) : HashSet<ConfiguredEffect>(effects)

fun Iterable<ConfiguredEffect>.inRunOrder(): Set<ConfiguredEffect> =
    if (this is EffectSet) this else {
        val list = mutableListOf<ConfiguredEffect>()

        // wah wah wah this is janky this sucks (I don't care)
        list += this.filter { it.effect.runOrder == RunOrder.START }
        list += this.filter { it.effect.runOrder == RunOrder.EARLY }
        list += this.filter { it.effect.runOrder == RunOrder.NORMAL }
        list += this.filter { it.effect.runOrder == RunOrder.LATE }
        list += this.filter { it.effect.runOrder == RunOrder.END }

        EffectSet(list)
    }

operator fun Iterable<ConfiguredEffect>.invoke(
    player: Player,
    data: TriggerData,
    namedArguments: Iterable<NamedArgument> = emptyList(),
    useTriggerPlayerForConditions: Boolean = false
) = this.forEach {
    it(
        BlankTrigger.createInvocation(player, data),
        acceptAllTriggers = true,
        namedArguments = namedArguments,
        useTriggerPlayerForConditions = useTriggerPlayerForConditions
    )
}

private object BlankTrigger : Trigger(
    "blank",
    TriggerParameter.values().toList()
) {
    fun createInvocation(player: Player, data: TriggerData): InvocationData {
        return InvocationData(
            player,
            data,
            BlankHolder,
            this,
            null,
            1.0
        )
    }
}
