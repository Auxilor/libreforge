package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.DefaultHashMap
import com.willfp.libreforge.Holder
import com.willfp.libreforge.HolderTemplate
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
import org.bukkit.entity.Player
import java.util.UUID

object EffectAddHolderToVictim : Effect<HolderTemplate>("add_holder_to_victim") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("effects", "You must specify the effects!")
        require("conditions", "You must specify the conditions!")
        require("duration", "You must specify the duration (in ticks)!")
    }

    private val holders = DefaultHashMap<UUID, MutableList<Holder>>(mutableListOf())

    init {
        registerHolderProvider { holders[it.uniqueId] }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: HolderTemplate) {
        val player = data.victim as? Player ?: return

        val duration = config.getIntFromExpression("duration", data)
        val holder = compileData.toHolder()

        holders[player.uniqueId] += holder

        plugin.scheduler.runLater(duration.toLong()) {
            holders[player.uniqueId] -= holder
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): HolderTemplate {
        val effects = Effects.compile(
            config.getSubsections("effects"),
            context.with("add_holder_to_victim Effects")
        )

        val conditions = Conditions.compile(
            config.getSubsections("conditions"),
            context.with("add_holder_to_victim Conditions")
        )

        return HolderTemplate(
            effects,
            conditions
        )
    }
}
