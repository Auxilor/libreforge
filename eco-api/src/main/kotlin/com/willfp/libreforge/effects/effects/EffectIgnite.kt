package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.metadata.FixedMetadataValue

class EffectIgnite: Effect(
    "ignite",
    true,
    Triggers.withParameters(
        TriggerParameter.VICTIM
    )
) {

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val damage = config.getDouble("damage-per-tick")
        val duration = config.getInt("ticks")

        victim.fireTicks = duration
        victim.setMetadata("ignitedMob", FixedMetadataValue(plugin, damage))
    }

    @EventHandler
    fun onBurn(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FIRE_TICK) return
        if (!event.entity.hasMetadata("ignitedMob")) return
        event.damage = event.entity.getMetadata("ignitedMob")[0].asDouble()
    }

}