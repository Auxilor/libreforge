package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.effects.Effect
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class EffectRewardKill : Effect(
    "reward_kill",
    supportsFilters = true
) {
    override fun onKill(
        killer: Player,
        victim: LivingEntity,
        event: EntityDeathByEntityEvent,
        config: JSONConfig
    ) {
        EconomyManager.giveMoney(killer, config.getDouble("amount"))
    }

    override fun validateConfig(config: JSONConfig): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getDoubleOrNull("amount")
            ?: violations.add(
                ConfigViolation(
                    "amount",
                    "You must specify the amount of money to give!"
                )
            )

        return violations
    }
}