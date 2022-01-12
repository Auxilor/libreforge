package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
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
    supportsFilters = false,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val amount = config.getIntFromExpression("amount")
        val height = config.getDoubleFromExpression("height", data.player)
        val radius = config.getDoubleFromExpression("radius", data.player)
        val damage = config.getDoubleFromExpression("arrow_damage", data.player)
        val flameTicks = config.getIntFromExpression("fire_ticks", data.player)

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
            if (damage != null) {
                arrow.damage = damage
            }
            if (flameTicks != null) {
                arrow.fireTicks = flameTicks
            }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of arrows!"
            )
        )

        if (!config.has("height")) violations.add(
            ConfigViolation(
                "height",
                "You must specify the height at which to spawn the arrows!"
            )
        )

        if (!config.has("radius")) violations.add(
            ConfigViolation(
                "radius",
                "You must specify the radius of the circle!"
            )
        )

        if (!config.has("damage")) violations.add(
            ConfigViolation(
                "damage",
                "You must specify the arrow damage!"
            )
        )

        if (!config.has("fire_ticks")) violations.add(
            ConfigViolation(
                "fire_ticks",
                "You must specify the arrow fire ticks!"
            )
        )

        return violations
    }
}