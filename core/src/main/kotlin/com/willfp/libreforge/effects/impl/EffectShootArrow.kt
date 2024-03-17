package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EffectShootArrow : Effect<NoCompileData>("shoot_arrow") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val velocity = data.velocity
        val fire = ((data.event as? EntityShootBowEvent)?.projectile?.fireTicks ?: 0) > 0

        player.runExempted {
            val arrow = if (velocity == null || !config.getBool("inherit_velocity")) {
                player.launchProjectile(Arrow::class.java)
            } else {
                player.launchProjectile(Arrow::class.java, velocity)
            }

            val damage = config.getOrNull("arrow_damage") { getDoubleFromExpression(it, data) }
            if (damage != null) {
                arrow.damage = damage
            }

            arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            if (fire) {
                arrow.fireTicks = Int.MAX_VALUE
            }

            if (config.getBool("no_source")) {
                arrow.shooter = null
            }

            if (config.getStringOrNull("effect") != null) {
                arrow.addCustomEffect(
                    PotionEffect(
                        PotionEffectType.getByName(config.getString("effect").uppercase())
                            ?: PotionEffectType.INCREASE_DAMAGE,
                        config.getIntFromExpression("duration", data),
                        config.getIntFromExpression("level", data) - 1,
                        true,
                        true,
                        true
                    ), false
                )
            }
        }

        return true
    }
}
