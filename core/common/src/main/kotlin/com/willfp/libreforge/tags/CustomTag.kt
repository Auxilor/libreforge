package com.willfp.libreforge.tags

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.matches
import com.willfp.eco.core.items.tag.CustomItemTag
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import org.bukkit.inventory.ItemStack

class CustomTag(
    config: Config,
    plugin: EcoPlugin
): CustomItemTag(plugin.createNamespacedKey(config.getString("id"))) {
    private val items = config.getStrings("items")
        .map { Items.lookup(it) }
        .filterNot { it is EmptyTestableItem }

    override fun matches(p0: ItemStack): Boolean {
        return items.matches(p0)
    }

    override fun getExampleItem(): ItemStack? {
        return items.firstOrNull()?.item
    }
}
