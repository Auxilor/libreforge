package com.willfp.libreforge.conditions.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class ItemCondition(id: String) : Condition<Collection<TestableItem>>(id) {
    override val arguments = arguments {
        require(listOf("items", "item"), "You must specify the items!")
    }

    override fun isMet(player: Player, config: Config, compileData: Collection<TestableItem>) =
        getItems(player).any { compileData.any { test -> test.matches(it) } }

    override fun makeCompileData(config: Config, context: ViolationContext): Collection<TestableItem> {
        return config.getStrings("items")
            .plusElement(config.getString("item"))
            .filter { it.isNotBlank() }
            .map { Items.lookup(it) }
    }

    abstract fun getItems(player: Player): Collection<ItemStack?>
}
