package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.ItemStack

object EffectDropRandomItem : Effect<List<ItemStack>>("drop_random_item") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("items", "You must specify the list of items to choose from!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: List<ItemStack>): Boolean {
        val location = data.location ?: return false
        val item = compileData.randomOrNull() ?: return false
        location.world?.dropItem(location, item)

        return true
    }

    override fun makeCompileData(config: Config, context: ViolationContext): List<ItemStack> {
        return config.getStrings("item")
            .map { Items.lookup(it) }
            .filterNot { it is EmptyTestableItem }
            .map { it.item }
    }
}
