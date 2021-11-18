package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.effects.Effect
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EffectDamageMultiplier : Effect("damage_multiplier") {
    override fun onAnyDamage(
        attacker: Player,
        victim: LivingEntity,
        event: EntityDamageByEntityEvent,
        config: JSONConfig
    ) {
        event.damage *= config.getDouble("multiplier")
    }
}