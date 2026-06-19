package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRotateVictim : Effect<NoCompileData>("rotate_victim") {
    override val description = "Rotates the victim's yaw by a specified angle."
    override val categories = setOf("movement", "combat")

    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require(
            "angle",
            "You must specify the angle to rotate by!",
            description = "The number of degrees to add to the victim's current yaw. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val yaw = victim.location.yaw + config.getDoubleFromExpression("angle", data).toFloat()

        victim.setRotation(
            yaw % 360f,
            victim.location.pitch
        )

        return true
    }
}
