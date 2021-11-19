package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.effects.ConfiguredEffect
import com.willfp.libreforge.api.effects.Effect
import com.willfp.libreforge.api.triggers.TriggerData
import com.willfp.libreforge.api.triggers.Triggers
import com.willfp.libreforge.api.triggers.wrappers.WrappedDamageEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.contracts.contract

class EffectDamageMultiplier : Effect(
    "damage_multiplier",
    supportsFilters = true,
    applicableTriggers = listOf(
        Triggers.TRIDENT_ATTACK,
        Triggers.BOW_ATTACK,
        Triggers.MELEE_ATTACK
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val player = data.player ?: return
        val victim = data.victim ?: return
        val event = data.event as? WrappedDamageEvent ?: return

        event.damage *= config.getDouble("multiplier")
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                ConfigViolation(
                    "multiplier",
                    "You must specify the damage multiplier!"
                )
            )

        return violations
    }
}