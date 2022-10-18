package com.willfp.libreforge.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class GenericItemCondition(
    id: String,
    private val getItem: (Player) -> ItemStack?
): Condition(id) {
    final override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        if (data !is ItemCompileData) {
            return true
        }

        return data.isMet(getItem(player))
    }

    final override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("items")) violations.add(
            ConfigViolation(
                "items",
                "You must specify the list of items!"
            )
        )

        return violations
    }

    final override fun makeCompileData(config: Config, context: String): CompileData {
        return ItemCompileData(config.getStrings("items").map {
            Items.lookup(it)
        })
    }

    private class ItemCompileData(
        override val data: List<TestableItem>
    ) : CompileData {
        fun isMet(itemStack: ItemStack?): Boolean {
            if (data.isEmpty()) {
                return true
            }

            return data.any { it.matches(itemStack) }
        }
    }
}
