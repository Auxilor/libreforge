package com.willfp.libreforge.conditions.conditions

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.CompileData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ConditionInMainhand : Condition("in_mainhand") {
    override fun isConditionMet(player: Player, config: Config, data: CompileData?): Boolean {
        if (data !is ItemCompileData) {
            return true
        }

        return data.isMet(player.inventory.itemInMainHand)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("items")) violations.add(
            ConfigViolation(
                "items",
                "You must specify the list of items!"
            )
        )

        return violations
    }

    override fun makeCompileData(config: Config, context: String): CompileData {
        return ItemCompileData(config.getStrings("items").map {
            Items.lookup(it)
        })
    }

    private class ItemCompileData(
        override val data: Iterable<TestableItem>
    ) : CompileData {
        fun isMet(itemStack: ItemStack?): Boolean {
            val list = data.toList()

            if (list.isEmpty()) {
                return true
            }

            return list.any { it.matches(itemStack) }
        }
    }
}