package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
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
        TriggerParameter.VICTIM,
        TriggerParameter.PLAYER
    )
) {

    override fun handle(data: TriggerData, config: Config) {
        val victim = data.victim ?: return
        val damage = config.getDoubleFromExpression("damage-per-tick", data.player)
        val duration = config.getIntFromExpression("ticks", data.player)

        victim.fireTicks = duration
        victim.setMetadata("ignitedMob", FixedMetadataValue(plugin, damage))
    }

    @EventHandler
    fun onBurn(event: EntityDamageEvent) {
        if (event.cause != EntityDamageEvent.DamageCause.FIRE_TICK) return
        if (!event.entity.hasMetadata("ignitedMob")) return
        event.damage = event.entity.getMetadata("ignitedMob")[0].asDouble()
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()
        if (!config.has("damage_per_tick")) violations.add(
            ConfigViolation(
                "damage_per_tick",
                "You must specify the damage of fire per tick!"
            )
        )
        if (!config.has("ticks")) violations.add(
            ConfigViolation(
                "ticks",
                "You must specify the duration of igniting in ticks!"
            )
        )
        return violations
    }

}
