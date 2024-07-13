package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow

object EffectPiercing : Effect<NoCompileData>("piercing") {
    override val arguments = arguments {
        require("level", "You must specify the pierce level!")
    }

    override val parameters = setOf(
        TriggerParameter.PROJECTILE
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val arrow = data.projectile as? AbstractArrow ?: return false

        arrow.pierceLevel += config.getIntFromExpression("level", data)

        return true
    }
}
