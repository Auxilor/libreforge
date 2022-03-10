package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.runExempted
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedShootBowEvent
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow

class EffectShootArrow : Effect(
    "shoot_arrow",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val velocity = data.velocity
        val fire = (data.event as? WrappedShootBowEvent)?.hasFire

        player.runExempted {
            val arrow = if (velocity == null || !config.getBool("inherit_velocity")) {
                it.launchProjectile(Arrow::class.java)
            } else {
                it.launchProjectile(Arrow::class.java, velocity)
            }

            arrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
            if (fire == true) {
                arrow.fireTicks = Int.MAX_VALUE
            }
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        return emptyList()
    }
}