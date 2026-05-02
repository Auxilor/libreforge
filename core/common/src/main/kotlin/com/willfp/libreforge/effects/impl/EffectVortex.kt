package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object EffectVortex : Effect<NoCompileData>("vortex") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("duration", "You must specify the pull duration in ticks!")
        require("damage", "You must specify the damage dealt at the end!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val origin = data.location?.clone() ?: return false
        val radius = config.getDoubleFromExpression("radius", data)
        val duration = config.getIntFromExpression("duration", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val pullStrength = config.getOrNull("pull_strength") { getDoubleFromExpression(it, data) } ?: 0.3

        val affected = mutableSetOf<LivingEntity>()
        var tick = 0

        plugin.runnableFactory.create { task ->
            tick++

            origin.world?.getNearbyEntities(origin, radius, radius, radius)
                ?.filterIsInstance<LivingEntity>()
                ?.filter { it != player }
                ?.forEach { entity ->
                    affected.add(entity)
                    val pull = origin.toVector()
                        .subtract(entity.location.toVector())
                        .normalize()
                        .multiply(pullStrength)
                    entity.velocity = entity.velocity.add(pull)
                }

            if (tick >= duration) {
                affected.forEach { it.damage(damage) }
                task.cancel()
            }
        }.runTaskTimer(0L, 1L)

        return true
    }
}
