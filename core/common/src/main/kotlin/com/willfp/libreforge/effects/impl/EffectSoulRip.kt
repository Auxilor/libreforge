package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

object EffectSoulRip : Effect<NoCompileData>("soul_rip") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("radius", "You must specify the radius!")
        require("damage", "You must specify the damage to deal!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = data.location ?: return false
        val radius = config.getDoubleFromExpression("radius", data)
        val damage = config.getDoubleFromExpression("damage", data)
        val healMultiplier = config.getOrNull("heal_multiplier") { getDoubleFromExpression(it, data) } ?: 1.0

        val targets = location.world?.getNearbyEntities(location, radius, radius, radius)
            ?.filterIsInstance<LivingEntity>()
            ?.filter { it != player }
            ?.takeIf { it.isNotEmpty() }
            ?: return false

        var totalHeal = 0.0
        for (entity in targets) {
            totalHeal += minOf(damage, entity.health)
            entity.damage(damage)
        }

        val maxHealth = player.getAttribute(Attribute.MAX_HEALTH)!!.value
        player.health = (player.health + totalHeal * healMultiplier).coerceAtMost(maxHealth)

        return true
    }
}
