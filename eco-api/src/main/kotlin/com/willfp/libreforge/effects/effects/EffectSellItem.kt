package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.economy.balance
import com.willfp.eco.core.integrations.shop.getPrice
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Material

class EffectSellItem : Effect(
    "sell_item",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
        TriggerParameter.ITEM
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val item = data.item ?: return

        val multiplier = if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data)
        } else 1.0

        val price = item.getPrice(player) * multiplier

        if (price > 0.0) {
            player.balance += price
            item.type = Material.AIR
            item.amount = 0
        }
    }
}
