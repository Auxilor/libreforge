package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.shop.getUnitValue
import com.willfp.eco.core.integrations.shop.isSellable
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.event.EditableDropEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EffectSellItems : Effect<Collection<TestableItem>?>("sell_items") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val runOrder = RunOrder.END

    override fun onTrigger(config: Config, data: TriggerData, compileData: Collection<TestableItem>?): Boolean {
        val player = data.player ?: return false
        val event = data.event as? EditableDropEvent
        val item = data.item

        val multiplier = if (config.has("multiplier")) {
            config.getDoubleFromExpression("multiplier", data)
        } else 1.0

        val items = (event?.items?.map { it.item } ?: listOf(item))
            .filterNotNull()
            .filter { compileData?.any { t -> t.matches(it) } ?: true }

        val sold = sell(player, items, multiplier)

        for (soldItem in sold) {
            if (item == soldItem) {
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
        }

        return sold
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableItem>? {
        return config.getStringsOrNull("whitelist")
            ?.map { Items.lookup(it) }
    }
}
