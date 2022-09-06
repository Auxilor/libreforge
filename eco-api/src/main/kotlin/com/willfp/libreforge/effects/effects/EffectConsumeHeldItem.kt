package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.updateEffects
import org.bukkit.Material

class EffectConsumeHeldItem : Effect(
    "consume_held_item",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val amount = config.getIntFromExpression("amount", data)

        val item = player.inventory.itemInMainHand

        val newAmount = item.amount - amount
        if (newAmount <= 0) {
            item.type = Material.AIR
        } else {
            item.amount = newAmount
        }

        player.inventory.setItemInMainHand(item)

        player.updateEffects()
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of items to consume!"
            )
        )

        return violations
    }
}
