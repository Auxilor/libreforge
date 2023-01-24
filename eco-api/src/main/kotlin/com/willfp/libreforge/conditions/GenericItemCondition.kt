package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class GenericItemCondition(
    id: String,
    private val getItems: (Player) -> Collection<ItemStack?>
): Condition(id) {
    override val arguments = arguments {
        require(listOf("item", "items"), "You must specify the item / list of items!")
    }

    final override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        if (data !is ItemCompileData) {
            return true
        }

        return getItems(player).any { data.isMet(it) }
    }

    final override fun makeCompileData(config: Config, context: ViolationContext): CompileData {
        return ItemCompileData((config.getStrings("items").map {
            Items.lookup(it)
        } + Items.lookup(config.getString("item"))).filterNot { it is EmptyTestableItem })
    }

    private class ItemCompileData(
        private val items: List<TestableItem>
    ) : CompileData {
        fun isMet(itemStack: ItemStack?): Boolean {
            if (items.isEmpty()) {
                return true
            }

            return items.any { it.matches(itemStack) }
        }
    }
}
