package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.Triggers

class EffectGiveMoney : Effect(
    "give_money",
    supportsFilters = true,
    applicableTriggers = listOf(
        Triggers.MINE_BLOCK,
        Triggers.KILL
    )
) {
    override fun handle(data: TriggerData, config: JSONConfig) {
        val player = data.player ?: return

        EconomyManager.giveMoney(player, config.getDouble("amount"))
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