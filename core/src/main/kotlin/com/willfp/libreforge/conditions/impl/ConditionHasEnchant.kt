package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.enchants.EnchantmentWithLevel
import com.willfp.libreforge.enchants.doesItemHaveEnchantmentAtLevel
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

object ConditionHasEnchant : Condition<NoCompileData>("has_enchant") {
    override val arguments = arguments {
        require(listOf("slot", "slots"), "You must specify the slot(s)!")
        require(listOf("enchant", "enchants"), "You must specify the enchant(s)!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val livingEntity = dispatcher.get<LivingEntity>() ?: return false
        val enchants = parseEnchants(config)
        val items = getItemsForSlotTypes(parseSlotTypes(config), livingEntity)
        return items.any { item ->
            enchants.all { it.doesItemHaveEnchantmentAtLevel(item) }
        }
    }

    private fun getItemsForSlotTypes(slotTypes: List<SlotType>, entity: LivingEntity) : List<ItemStack> {
        return slotTypes.map { it.getItems(entity) }.flatten()
    }

    private fun parseSlotTypes(config: Config) : List<SlotType> {
        val slots = config.getStrings("slots", "slot").filterNot { it.isBlank() }
        return slots.mapNotNull { slot ->
            SlotTypes.get(slot)
        }
    }
    private fun parseEnchants(config: Config) : List<EnchantmentWithLevel> {
        val enchants = config.getStrings("enchants", "enchant").filterNot { it.isBlank() }
        return enchants.mapNotNull { enchant ->
            if (enchant.contains(":")) {
                val (enchantmentName, enchantmentLevel) = enchant.split(":", limit = 2)
                val enchantment = getEnchantmentByKey(enchantmentName) ?: return@mapNotNull null
                val levels = parseEnchantmentLevels(enchantmentLevel)
                EnchantmentWithLevel(enchantment = enchantment, levels = levels)
            } else {
                val enchantment = getEnchantmentByKey(enchant) ?: return@mapNotNull null
                EnchantmentWithLevel(enchantment = enchantment)
            }
        }
    }

    private fun parseEnchantmentLevels(enchantmentLevel: String): List<Int> {
        if (enchantmentLevel.toIntOrNull() != null) {
            return listOf(enchantmentLevel.toInt())
        } else {
            if (enchantmentLevel.contains("-")) {
                val bounds = enchantmentLevel.split("-").mapNotNull { it.toIntOrNull() }
                if (bounds.size == 2 && bounds[0] <= bounds[1]) {
                    return (bounds[0] .. bounds[1]).toList()
                }
            }
            // Return value which is out of bounds for minecraft enchantments so the user has no trigger outputs,
            // forcing them to check the format of their level.
            return listOf(Integer.MAX_VALUE)
        }
    }

    private fun getEnchantmentByKey(enchant: String): Enchantment? {
        val minecraftNamespace = NamespacedKey.minecraft(enchant)
        return Enchantment.getByKey(minecraftNamespace)
    }

}