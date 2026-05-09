package com.willfp.libreforge.tags

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.blocks.Blocks
import com.willfp.eco.core.blocks.impl.EmptyTestableBlock
import com.willfp.eco.core.blocks.matches
import com.willfp.eco.core.blocks.tag.CustomBlockTag as EcoCustomBlockTag
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.entities.Entities
import com.willfp.eco.core.entities.impl.EmptyTestableEntity
import com.willfp.eco.core.entities.matches
import com.willfp.eco.core.entities.tag.CustomEntityTag as EcoCustomEntityTag
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.matches
import com.willfp.eco.core.items.tag.CustomItemTag
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import org.bukkit.block.Block
import org.bukkit.entity.Entity
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

class CustomBlockTag(
    config: Config,
    plugin: EcoPlugin
) : EcoCustomBlockTag(plugin.createNamespacedKey(config.getString("id"))) {
    private val blocks = config.getStrings("blocks")
        .map { Blocks.lookup(it) }
        .filterNot { it is EmptyTestableBlock }

    override fun matches(block: Block): Boolean {
        return blocks.matches(block)
    }
}

class CustomEntityTag(
    config: Config,
    plugin: EcoPlugin
) : EcoCustomEntityTag(plugin.createNamespacedKey(config.getString("id"))) {
    private val entities = config.getStrings("entities")
        .map { Entities.lookup(it) }
        .filterNot { it is EmptyTestableEntity }

    override fun matches(entity: Entity): Boolean {
        return entities.matches(entity)
    }
}