package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.Holder
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
import org.bukkit.entity.Player
import java.util.UUID

class EffectAddHolderToVictim : Effect(
    "add_holder_to_victim",
    triggers = Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {
    private val holders = mutableMapOf<UUID, MutableMap<UUID, Holder>>()

    init {
        plugin.registerHolderProvider {
            val current = holders[it.uniqueId] ?: emptyMap()
            current.values
        }
    }

    override fun handle(invocation: InvocationData, config: Config) {
        val player = invocation.data.victim as? Player ?: return
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

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("effects")) violations.add(
            ConfigViolation(
                "effects",
                "You must specify the effects!"
            )
        )

        if (!config.has("conditions")) violations.add(
            ConfigViolation(
                "conditions",
                "You must specify the conditions!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration (in ticks)!"
            )
        )

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            "$context -> add_holder Effects"
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            "$context -> add_holder Conditions"
        )

        return UnfinishedHolder(
            effects,
            conditions
        )
    }

    private data class UnfinishedHolder(
        val effects: Set<ConfiguredEffect>,
        val conditions: Set<ConfiguredCondition>
    ) : CompileData

    private class AddedHolder(
        override val effects: Set<ConfiguredEffect>,
        override val conditions: Set<ConfiguredCondition>,
        override val id: String
    ) : Holder
}
