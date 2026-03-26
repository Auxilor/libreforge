package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.event.entity.EntityShootBowEvent

object EffectShootArrow : Effect<NoCompileData>("shoot_arrow") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val velocity = data.velocity
        val fire = ((data.event as? EntityShootBowEvent)?.projectile?.fireTicks ?: 0) > 0

        val arrow = if (velocity == null || !config.getBool("inherit_velocity")) {
            player.launchProjectile(Arrow::class.java)
        } else {
            player.launchProjectile(Arrow::class.java, velocity)
        }

        if (config.getBool("launch-at-location") && data.location != null) {
            arrow.teleportAsync(data.location)
        }

        arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        if (fire) {
            arrow.fireTicks = Int.MAX_VALUE
        }

        if (config.getBool("no_source")) {
            arrow.shooter = null
        }

        return true
    }
}
