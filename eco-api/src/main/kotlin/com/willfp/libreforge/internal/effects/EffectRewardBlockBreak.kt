package com.willfp.libreforge.internal.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.libreforge.api.effects.Effect
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

class EffectRewardBlockBreak : Effect("reward_block_break") {
    override fun onBlockBreak(
        player: Player,
        block: Block,
        event: BlockBreakEvent,
        config: JSONConfig
    ) {
        EconomyManager.giveMoney(player, config.getDouble("amount"))
    }
}