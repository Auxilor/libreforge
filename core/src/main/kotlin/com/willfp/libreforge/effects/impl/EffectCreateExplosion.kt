package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectCreateExplosion : Effect(
    "create_explosion",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of explosions!")
        require("power", "You must specify the explosion power!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val amount = config.getIntFromExpression("amount", data)
        val power = config.getDoubleFromExpression("power", data)

        for (i in 1..amount) {
            plugin.scheduler.runLater(i.toLong()) {
                world.createExplosion(location, power.toFloat())
            }
        }
    }
}
