package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.shop.getUnitValue
import com.willfp.eco.core.integrations.shop.isSellable
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import com.willfp.libreforge.triggers.wrappers.WrappedDropEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class EffectSellItems : Effect(
    "sell_items",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    ),
    runOrder = RunOrder.END
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return
        val event = data.event as? WrappedDropEvent<*>
        val item = data.item

        val multiplier = if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data)
        } else 1.0

        val whitelist = config.getStringsOrNull("whitelist")
            ?.map { Items.lookup(it) }

        val items = (event?.finalItems ?: listOf(item))
            .filterNotNull()
            .filter { whitelist?.any { t -> t.matches(it) } ?: true }

        val sold = sell(player, items, multiplier)

        for (soldItem in sold) {
            if (item == soldItem) {
                item.type = Material.AIR
                item.amount = 0
            }
            event?.removeItem(soldItem)
        }
    }

    private fun sell(player: Player, items: Iterable<ItemStack>, multiplier: Double): Collection<ItemStack> {
        val sold = mutableListOf<ItemStack>()

        for (item in items) {
            if (!item.isSellable(player)) {
                continue
            }

            val price = item.getUnitValue(player)
            price.giveTo(player, item.amount * multiplier)
        }

        return sold
    }
}
