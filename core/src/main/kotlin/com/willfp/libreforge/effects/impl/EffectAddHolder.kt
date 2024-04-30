package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.SimpleProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.nest
import com.willfp.libreforge.plugin
import com.willfp.libreforge.registerGenericHolderProvider
import com.willfp.libreforge.triggers.TriggerData
import java.util.UUID

object EffectAddHolder : Effect<HolderTemplate>("add_holder") {
    override val isPermanent = false

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("duration", "You must specify the duration (in ticks)!")
    }

    private val holders = listMap<UUID, Holder>()

    fun getHolders(): Map<UUID, List<Holder>> {
        return holders
    }

    init {
        registerGenericHolderProvider {
            holders[it.uuid].map { h -> SimpleProvidedHolder(h) }
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate): Boolean {
        val dispatcher = data.dispatcher
        val duration = config.getIntFromExpression("duration", data).coerceAtLeast(1)
        val holder = compileData.toHolder().nest(data.holder)

        holders[dispatcher.uuid] += holder

        plugin.scheduler.runLater(duration.toLong()) {
            holders[dispatcher.uuid] -= holder
            if (holders[dispatcher.uuid].isEmpty()) {
                holders -= dispatcher.uuid
            }
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder conditions")
        )

        return HolderTemplate(
            effects,
            conditions
        )
    }
}
