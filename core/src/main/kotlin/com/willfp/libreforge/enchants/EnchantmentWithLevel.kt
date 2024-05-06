package com.willfp.libreforge.enchants

import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

data class EnchantmentWithLevel(
    val enchantment: Enchantment,
    val levels: List<Int> = emptyList()
)

fun EnchantmentWithLevel.doesItemHaveEnchantmentAtLevel(item: ItemStack): Boolean =
    item.containsEnchantment(enchantment) && (levels.isEmpty() || item.getEnchantmentLevel(enchantment) in levels)
