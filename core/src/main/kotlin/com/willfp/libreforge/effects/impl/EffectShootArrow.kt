package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
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
                player.launchProjectile(Arrow::class.java, player.velocity)
            } else {
                player.launchProjectile(Arrow::class.java, velocity)
            }

            if (config.getBool("launch-at-location") && data.location != null) {
                arrow.teleportAsync(data.location)
            }

            arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            arrow.pierceLevel = 1
            arrow.setBounce(false)
            if (config.getDoubleOrNull("damage") != null) {
                arrow.damage = config.getDoubleFromExpression("damage")
            }
            else {
                arrow.damage = 6.0
            }

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
