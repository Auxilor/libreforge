package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

class EffectIgnite: Effect(
    "ignite",
    Triggers.withParameters(
        TriggerParameter.VICTIM,
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("damage_per_tick", "You must specify the damage per fire tick!")
        require("ticks", "You must specify the duration!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val damage = config.getDoubleFromExpression("damage_per_tick", data)
        val duration = config.getIntFromExpression("ticks", data)

        victim.fireTicks = duration
        victim.setMetadata("libreforge-ignite", plugin.createMetadataValue(damage))
    }

    @EventHandler
    fun onBurn(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FIRE_TICK) {
            return
        }
        if (!event.entity.hasMetadata("libreforge-ignite")) {
            return
        }

        event.damage = event.entity.getMetadata("ignitedMob")[0].asDouble()
    }
}
