package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.price.impl.PriceItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectRemoveItem : Effect<TestableItem>("remove_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("item", "You must specify the item to remove!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: TestableItem): Boolean {
        val player = data.player ?: return false

        if (compileData is EmptyTestableItem) {
            return false
        }

        val amount = compileData.item.amount

        PriceItem(amount, compileData).pay(player)

        return false
    }

    override fun makeCompileData(config: Config, context: ViolationContext): TestableItem {
        return Items.lookup(config.getString("item"))
    }
}
