package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.util.Vector

object EffectSetVictimVelocity : Effect<NoCompileData>("set_victim_velocity") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("x", "You must specify the velocity x component!")
        require("y", "You must specify the velocity y component!")
        require("z", "You must specify the velocity z component!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        victim.velocity = Vector(
            config.getDoubleFromExpression("x", data),
            config.getDoubleFromExpression("y", data),
            config.getDoubleFromExpression("z", data)
        )

        return true
    }
}
