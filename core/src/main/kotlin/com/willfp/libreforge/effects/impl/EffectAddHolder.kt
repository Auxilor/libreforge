package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
import com.willfp.libreforge.ListedHashMap
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.registerHolderProvider
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import java.util.UUID

object EffectAddHolder : Effect<HolderTemplate>("add_holder") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("duration", "You must specify the duration (in ticks)!")
    }

    private val holders = ListedHashMap<UUID, Holder>()

    init {
        registerHolderProvider { holders[it.uniqueId] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate): Boolean {
        val player = data.player ?: return false

        val duration = config.getIntFromExpression("duration", data)
        val holder = compileData.toHolder()

        holders[player.uniqueId] += holder

        plugin.scheduler.runLater(duration.toLong()) {
            holders[player.uniqueId] -= holder
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder Conditions")
        )

        return HolderTemplate(
            effects,
            conditions
        )
    }
}
