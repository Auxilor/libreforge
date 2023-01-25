package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Holder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.conditions.ConfiguredCondition
import com.willfp.libreforge.effects.CompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import java.util.UUID

class EffectAddHolder : Effect(
    "add_holder",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("duration", "You must specify the duration (in ticks)!")
    }

    private val holders = mutableMapOf<UUID, MutableMap<UUID, Holder>>()

    init {
        plugin.registerHolderProvider {
            val current = holders[it.uniqueId] ?: emptyMap()
            current.values
        }
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val player = invocation.data.player ?: return
        val unfinished = invocation.compileData as? UnfinishedHolder ?: return

        val uuid = UUID.randomUUID()

        val holder = AddedHolder(
            unfinished.effects,
            unfinished.conditions,
            "add_holder:$uuid"
        )

        val duration = config.getIntFromExpression("duration", invocation.data)

        val current = holders[player.uniqueId] ?: mutableMapOf()
        current[uuid] = holder
        holders[player.uniqueId] = current

        plugin.scheduler.runLater(duration.toLong()) {
            val new = holders[player.uniqueId] ?: mutableMapOf()
            new.remove(uuid)
            holders[player.uniqueId] = new
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder Conditions")
        )

        return UnfinishedHolder(
            effects,
            conditions
        )
    }

    private data class UnfinishedHolder(
        val effects: List<ConfiguredEffect>,
        val conditions: List<ConfiguredCondition>
    ) : CompileData

    private class AddedHolder(
        override val effects: List<ConfiguredEffect>,
        override val conditions: List<ConfiguredCondition>,
        override val id: String
    ) : Holder
}
