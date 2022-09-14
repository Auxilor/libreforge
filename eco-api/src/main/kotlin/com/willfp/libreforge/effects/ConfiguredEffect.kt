package com.willfp.libreforge.effects;

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.events.EffectPreActivateEvent
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.ConfiguredDataMutator
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.mutate
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max

private val everyHandler = mutableMapOf<UUID, Int>()

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
        effect.handleEnable(player, args, generator.makeIdentifiers(offset))
    }

    fun disableFor(player: Player) {
        val offset = effectStack[player.uniqueId] ?: 0
        effect.handleDisable(player, generator.makeIdentifiers(offset))
        effectStack[player.uniqueId] = max(offset - 1, 0)
    }

    internal operator fun invoke(
        rawInvocation: InvocationData,
        ignoreTriggerList: Boolean = false,
        namedArguments: Iterable<NamedArgument> = emptyList(),
    ) {
        if (!ignoreTriggerList) {
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
            if (!condition.isMet(invocation.player)) {
                unmetConditions.add(condition)
            }
        }

        val conditionsAreMet = unmetConditions.isEmpty()

        val (player, data, holder, _) = invocation

        if (args.has("chance")) {
            if (NumberUtils.randFloat(0.0, 100.0) > args.getDoubleFromExpression("chance", player)) {
                return
            }
        }

        if (data.player != null && data.victim != null) {
            if (!args.getBool("disable_antigrief_check")) {
                if (!AntigriefManager.canInjure(data.player, data.victim)) {
                    return
                }
            }
        }

        if (args.getBool("filters_before_mutation")) {
            if (!Filter.matches(rawInvocation.data, filter)) {
                return
            }
        } else {
            if (!Filter.matches(data, filter)) {
                return
            }
        }

        val every = if (args.has("every")) args.getIntFromExpression("every", player) else 0

        if (every > 0) {
            var current = everyHandler[uuid] ?: 0
            val prev = current

            // Don't increment every if conditions aren't met
            if (conditionsAreMet) {
                current++
            }

            if (current >= every) {
                current = 0
            }

            everyHandler[uuid] = current

            if (prev != 0) {
                return
            }
        }

        // Don't bother with events if the condition is not met
        if (conditionsAreMet) {
            val preActivateEvent = EffectPreActivateEvent(player, holder, effect, args)
            LibReforgePlugin.instance.server.pluginManager.callEvent(preActivateEvent)

            if (preActivateEvent.isCancelled) {
                return
            }
        }

        if (effect.getCooldown(player, uuid) > 0) {
            if (args.getBoolOrNull("send_cooldown_message") != false) {
                // Don't send message if conditions aren't met

                if (conditionsAreMet) {
                    effect.sendCooldownMessage(player, uuid)
                }
            }
            return
        }

        if (args.has("cost")) {
            val cost = args.getDoubleFromExpression("cost", player)
            if (!EconomyManager.hasAmount(player, cost)) {
                effect.sendCannotAffordMessage(player, cost)
                return
            }

            // Don't remove money if conditions aren't met
            if (conditionsAreMet) {
                EconomyManager.removeMoney(player, cost)
            }
        }


        if (!conditionsAreMet) {
            for (condition in unmetConditions) {
                for (notMetEffect in condition.notMetEffects) {
                    notMetEffect(
                        invocation,
                        ignoreTriggerList = true,
                        namedArguments = namedArguments
                    )
                }
            }

            return
        }

        val activateEvent = EffectActivateEvent(player, holder, effect, args)
        LibReforgePlugin.instance.server.pluginManager.callEvent(activateEvent)

        if (activateEvent.isCancelled) {
            return
        }

        effect.resetCooldown(player, args, uuid)

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
    if (this is EffectSet) this else EffectSet(this.filter { !it.effect.runsLast } +
            this.filter { it.effect.runsLast })
