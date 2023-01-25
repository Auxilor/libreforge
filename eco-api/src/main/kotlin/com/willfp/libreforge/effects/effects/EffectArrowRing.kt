package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Arrow
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class EffectArrowRing : Effect(
    "arrow_ring",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of arrows!")
        require("height", "You must specify the height to spawn the arrows at!")
        require("radius", "You must specify the radius of the circle!")
        require("arrow_damage", "You must specify the arrow damage!")
        require("fire_ticks", "You must specify the arrow fire ticks!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val amount = config.getIntFromExpression("amount", data)
        val height = config.getDoubleFromExpression("height", data)
        val radius = config.getDoubleFromExpression("radius", data)
        val damage = config.getDoubleFromExpression("arrow_damage", data)
        val flameTicks = config.getIntFromExpression("fire_ticks", data)

        if (data.player != null) {
            if (!location.getNearbyPlayers(radius + 0.5f).all { AntigriefManager.canInjure(data.player, it) }) {
                return
            }
        }

        val apex = location.clone().add(0.0, height, 0.0)

        val angle = PI * 2 / amount

        for (i in 0 until amount) {
            val spawn = apex.clone()
            spawn.add(
                sin(angle * i) * radius,
                0.0,
                cos(angle * i) * radius
            )
            val arrow = world.spawn(
                spawn,
                Arrow::class.java
            )
            arrow.velocity = Vector(0, -1, 0)
            arrow.damage = damage
            arrow.fireTicks = flameTicks
        }
    }
}
