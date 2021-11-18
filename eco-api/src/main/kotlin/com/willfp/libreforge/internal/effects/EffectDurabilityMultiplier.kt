package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.api.effects.Effect
import org.bukkit.event.player.PlayerItemDamageEvent
import kotlin.math.ceil

class EffectDurabilityMultiplier : Effect("durability_multiplier") {
    override fun onDurabilityDamage(
        event: PlayerItemDamageEvent,
        config: JSONConfig
    ) {
        val multiplier = config.getDouble("multiplier")
        if (multiplier == 0.0) {
            event.isCancelled = true
            return
        }

        if (multiplier < 1) {
            event.damage *= ceil(1 / multiplier).toInt()
            return
        }

        val chance = 1 - (1 / multiplier)
        if (NumberUtils.randFloat(0.0, 1.0) < chance) {
            event.isCancelled = true
        }
    }
}