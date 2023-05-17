package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.price.ConfiguredPrice
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.entities.impl.CustomEntityPricePickup
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack

object EffectDropPriceItem : Effect<ItemStack>("drop_price_item") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("item", "You must specify the item to drop!")
        require("price", "You must specify the price given on pickup!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: ItemStack): Boolean {
        val location = data.location ?: return false

        val price = ConfiguredPrice.create(
            config.getSubsection("price")
        ) ?: return false

        val isGlowing = config.getBool("glow")

        CustomEntityPricePickup(price, compileData, isGlowing).spawn(location)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): ItemStack {
        return Items.lookup(config.getString("item")).item
    }
}
