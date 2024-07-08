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
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

object EffectIgnite : Effect<NoCompileData>("ignite") {
    override val parameters = setOf(
        TriggerParameter.VICTIM,
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("damage_per_tick", "You must specify the damage per fire tick!")
        require("ticks", "You must specify the duration!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val damage = config.getDoubleFromExpression("damage_per_tick", data)
        val duration = config.getIntFromExpression("ticks", data)

        victim.fireTicks = duration
        victim.setMetadata("libreforge-ignite", plugin.createMetadataValue(damage))

        return true
    }

    @EventHandler
    fun onBurn(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FIRE_TICK) {
            return
        }
        if (!event.entity.hasMetadata("libreforge-ignite")) {
            return
        }

        event.damage = event.entity.getMetadata("libreforge-ignite")[0].asDouble()
    }
}
