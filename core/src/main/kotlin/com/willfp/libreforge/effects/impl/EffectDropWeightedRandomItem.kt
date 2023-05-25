package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.WeightedItems
import com.willfp.libreforge.WeightedList
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toWeightedList
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectDropWeightedRandomItem : Effect<WeightedList<WeightedItems>>("drop_weighted_random_item") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("items", "You must specify the list of items to choose from!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: WeightedList<WeightedItems>): Boolean {
        val location = data.location ?: return false
        val player = data.player
        val items = compileData.randomOrNull() ?: return false
        val item = items.obj.randomOrNull() ?: return false

        if (player != null) {
            DropQueue(player)
                .addItem(item)
                .setLocation(location)
                .push()
        } else {
            location.world?.dropItem(location, item)
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): WeightedList<WeightedItems> {
        val items = mutableListOf<WeightedItems>()

        for (section in config.getSubsections("items")) {
            items += WeightedItems(
                section.getStrings("items", "item")
                    .map { Items.lookup(it) }
                    .filterNot { it is EmptyTestableItem }
                    .map { it.item },
                section.getDouble("weight")
            )
        }

        return items.toWeightedList()
    }
}
