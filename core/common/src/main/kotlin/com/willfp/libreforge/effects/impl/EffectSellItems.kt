package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.shop.getUnitValue
import com.willfp.eco.core.integrations.shop.isSellable
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EffectSellItems : Effect<Collection<TestableItem>?>("sell_items") {
    override val description = "Sells sellable items from the player's inventory or drop event, crediting the player with money."
    override val categories = setOf("economy", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "multiplier",
            description = "A multiplier applied to the sell price of each item. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1.0"
        )
        optional(
            "whitelist",
            description = "A list of item types that are allowed to be sold. If omitted, all sellable items are sold.",
            type = ArgType.ITEM_LIST
        )
    }

    override val runOrder = RunOrder.END

    override fun onTrigger(config: Config, data: TriggerData, compileData: Collection<TestableItem>?): Boolean {
        val player = data.player ?: return false
        val event = data.event as? EditableDropEvent
        val item = data.item

        val multiplier = config.getOrElse("multiplier", 1.0) { getDoubleFromExpression(it, data) }

        val items = (event?.items?.map { it.item } ?: listOf(item))
            .filterNotNull()
            .filter { compileData?.any { t -> t.matches(it) } ?: true }

        val sold = sell(player, items, multiplier)

        for (soldItem in sold) {
            if (item == soldItem) {
                @Suppress("DEPRECATION")
                item.type = Material.AIR
                item.amount = 0
            }
            event?.removeItem(soldItem)
        }

        return true
    }

    private fun sell(player: Player, items: Iterable<ItemStack>, multiplier: Double): Collection<ItemStack> {
        val sold = mutableListOf<ItemStack>()

        for (item in items) {
            if (!item.isSellable(player)) {
                continue
            }

            val price = item.getUnitValue(player)
            price.giveTo(player, item.amount * multiplier)
            sold.add(item)
        }

        return sold
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableItem>? {
        return config.getStringsOrNull("whitelist")
            ?.map { Items.lookup(it) }
    }
}
