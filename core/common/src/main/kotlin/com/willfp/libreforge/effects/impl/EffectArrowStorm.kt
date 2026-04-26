package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.util.Vector

object EffectArrowStorm : Effect<NoCompileData>("arrow_storm") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("amount", "You must specify the number of arrows!")
        require("height", "You must specify the spawn height above target!")
        require("spread", "You must specify the spread radius!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false
        val amount = config.getIntFromExpression("amount", data)
        val height = config.getDoubleFromExpression("height", data)
        val spread = config.getDoubleFromExpression("spread", data)
        val damage = config.getOrNull("damage") { getDoubleFromExpression(it, data) }

        repeat(amount) {
            val spawnLoc = location.clone().add(
                NumberUtils.randFloat(-spread, spread),
                height,
                NumberUtils.randFloat(-spread, spread)
            )
            val arrow = world.spawn(spawnLoc, Arrow::class.java)
            arrow.velocity = Vector(0.0, -2.0, 0.0)
            arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            if (damage != null) arrow.damage = damage
        }

        return true
    }
}
