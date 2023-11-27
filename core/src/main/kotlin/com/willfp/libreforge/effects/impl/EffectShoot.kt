package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityShootBowEvent

@Suppress("UNCHECKED_CAST")
object EffectShoot : Effect<NoCompileData>("shoot") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("projectile", "You must specify the projectile!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val velocity = data.velocity
        val fire = ((data.event as? EntityShootBowEvent)?.projectile?.fireTicks ?: 0) > 0

        val projectileClass = enumValueOfOrNull<EntityType>(config.getString("projectile").uppercase())?.entityClass
            ?: return false

        player.runExempted {
            val projectile = if (velocity == null || !config.getBool("inherit_velocity")) {
                player.launchProjectile(projectileClass as Class<out Projectile>)
            } else {
                player.launchProjectile(projectileClass as Class<out Projectile>, velocity)
            }

            if (config.getBool("launch-at-location") && data.location != null && projectile !is AbstractArrow) {
                projectile.teleportAsync(data.location)
            }

            if (projectile is AbstractArrow) {
                projectile.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            }

            if (fire) {
                projectile.fireTicks = Int.MAX_VALUE
            }

            if (config.getBool("no_source")) {
                projectile.shooter = null
            }
        }

        return true
    }
}
