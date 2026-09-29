package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.WeightedItems
import com.willfp.libreforge.WeightedList
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.toWeightedList
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveWeightedRandomItem : Effect<WeightedList<WeightedItems>>("give_weighted_random_item") {
    override val description = "Gives the player one item chosen from a weighted list, optionally placing it into a specific inventory slot."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "items",
            "You must specify the list of items to choose from!",
            description = "A list of weighted item groups. Each entry has a weight and an items list.",
            type = ArgType.DYNAMIC,
            schema = WeightedItemSpec::class
        )
        optional(
            "slot",
            description = "The inventory slot type to place the item into. If omitted the item is dropped into the player's inventory via telekinesis.",
            type = ArgType.STRING,
            example = "mainhand"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: WeightedList<WeightedItems>): Boolean {
        val player = data.player ?: return false
        val item = compileData.randomOrNull()?.items?.randomOrNull() ?: return false

        SlotTypes[config.getFormattedString("slot", data)]?.addToSlot(player, item) ?: run {
            DropQueue(player)
                .addItem(item)
                .forceTelekinesis()
                .push()
        }

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): WeightedList<WeightedItems> {
        return config.getSubsections("items").map { section ->
            WeightedItems(
                section.getStrings("items", "item")
                    .map { Items.lookup(it) }
                    .filterNot { it is EmptyTestableItem }
                    .map { it.item },
                section.getDouble("weight")
            )
        }.toWeightedList()
    }
}
