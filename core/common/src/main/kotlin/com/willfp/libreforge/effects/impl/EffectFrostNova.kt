package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EffectFrostNova : Effect<NoCompileData>("frost_nova") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("freeze_ticks", "You must specify the freeze duration in ticks!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val player = data.player
        val radius = config.getDoubleFromExpression("radius", data)
        val freezeTicks = config.getIntFromExpression("freeze_ticks", data)
        val slowDuration = config.getOrNull("slow_duration") { getIntFromExpression(it, data) } ?: 0
        val slowAmplifier = config.getOrNull("slow_amplifier") { getIntFromExpression(it, data) } ?: 0

        location.world?.getNearbyEntities(location, radius, radius, radius)
            ?.filterIsInstance<LivingEntity>()
            ?.filter { it != player }
            ?.forEach { entity ->
                entity.freezeTicks = freezeTicks
                if (slowDuration > 0) {
                    entity.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, slowDuration, slowAmplifier))
                }
            }

        return true
    }
}
