package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectShockwave : Effect<NoCompileData>("shockwave") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the shockwave radius!")
        require("pulses", "You must specify the number of pulses!")
        require("damage", "You must specify the damage per entity!")
        require("knockback", "You must specify the knockback force!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val origin = player.location.clone()
        val radius = config.getDoubleFromExpression("radius", data)
        val pulses = config.getIntFromExpression("pulses", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val knockback = config.getDoubleFromExpression("knockback", data)

        val hit = mutableSetOf<LivingEntity>()
        var pulse = 0

        plugin.runnableFactory.create { task ->
            pulse++
            val currentRadius = radius * pulse / pulses

            origin.world?.getNearbyEntities(origin, currentRadius, currentRadius, currentRadius)
                ?.filterIsInstance<LivingEntity>()
                ?.filter { it !in hit && it != player }
                ?.forEach { entity ->
                    hit.add(entity)
                    val dir = entity.location.toVector()
                        .subtract(origin.toVector())
                        .normalize()
                    entity.velocity = dir.multiply(knockback)
                    entity.damage(damage)
                }

            if (pulse >= pulses) task.cancel()
        }.runTaskTimer(0L, 3L)

        return true
    }
}
