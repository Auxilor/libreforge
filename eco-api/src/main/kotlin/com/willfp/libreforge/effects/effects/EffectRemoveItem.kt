package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.price.impl.PriceItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectRemoveItem: Effect(
    "remove_item",
    triggers = Triggers.withParameters(TriggerParameter.PLAYER)
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val item = Items.lookup(config.getString("item"))
        val amount = item.item.amount

        if (item is EmptyTestableItem) {
            return
        }

        PriceItem(amount, item).pay(player)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("item")) violations.add(
            ConfigViolation(
                "item",
                "You must specify an item lookup string to remove! " +
                        "https://plugins.auxilor.io/all-plugins/the-item-lookup-system"
            )
        )

        return violations
    }
}
