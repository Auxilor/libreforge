package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.price.impl.PriceItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectRemoveItem: Effect(
    "remove_item",
    triggers = Triggers.withParameters(TriggerParameter.PLAYER)
) {
    override val arguments = arguments {
        require("item", "You must specify the item to remove!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val item = Items.lookup(config.getString("item"))
        val amount = item.item.amount

        if (item is EmptyTestableItem) {
            return
        }

        PriceItem(amount, item).pay(player)
    }
}
