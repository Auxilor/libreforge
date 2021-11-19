package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.effects.Effect
import com.willfp.libreforge.api.triggers.TriggerData
import com.willfp.libreforge.api.triggers.Triggers
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

class EffectGiveMoney : Effect(
    "give_money",
    supportsFilters = true,
    applicableTriggers = listOf(Triggers.MINE_BLOCK)
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