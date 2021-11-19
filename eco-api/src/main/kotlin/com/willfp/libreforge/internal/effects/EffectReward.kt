package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.api.ConfigViolation
import com.willfp.libreforge.api.effects.Effect
import com.willfp.libreforge.api.triggers.Triggers
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

class EffectReward : Effect(
    "reward_block_break",
    supportsFilters = true,
    applicableTriggers = listOf(Triggers.MINE_BLOCK)
) {
    override fun onBlockBreak(
        player: Player,
        block: Block,
        event: BlockBreakEvent,
        config: JSONConfig
    ) {
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