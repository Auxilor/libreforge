package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigArguments
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Monster

object EffectSwarm : Effect<NoCompileData>("swarm") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("radius", "You must specify the maximum distance to swarm the victim from!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val radius = config.getDoubleFromExpression("radius", data)

        victim.getNearbyEntities(radius, radius, radius)
            .filterIsInstance<Monster>()
            .forEach { it.target = victim }

        return true
    }
}
