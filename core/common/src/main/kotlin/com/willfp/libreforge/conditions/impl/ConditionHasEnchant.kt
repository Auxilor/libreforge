package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.slot.SlotType
import com.willfp.libreforge.slot.SlotTypes
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
            enchants.all { it.matches(item) }
        }
    }

    private fun getItemsForSlotTypes(slotTypes: List<SlotType>, entity: LivingEntity) : List<ItemStack> {
        return slotTypes.map { it.getItems(entity) }.flatten()
    }

    private fun parseSlotTypes(config: Config) : List<SlotType> {
        val slots = config.getStrings("slots", "slot")

        return slots.mapNotNull { SlotTypes[it] }
    }

    private fun parseEnchants(config: Config) : List<RangedEnchantmentLevel> {
        val enchants = config.getStrings("enchants", "enchant")

        return enchants.mapNotNull { key ->
            val split = key.split(":")
            val enchant = getEnchantment(split[0]) ?: return@mapNotNull null

            val levels = if (split.size > 1) {
                parseLevelRange(split[1])
            } else {
                null
            }

            RangedEnchantmentLevel(enchant, levels)
        }
    }

    private fun parseLevelRange(enchantmentLevel: String): IntRange {
        if (enchantmentLevel.toIntOrNull() != null) {
            return enchantmentLevel.toInt()..enchantmentLevel.toInt()
        } else {
            if (enchantmentLevel.contains("-")) {
                val bounds = enchantmentLevel.split("-").mapNotNull { it.toIntOrNull() }
                if (bounds.size == 2 && bounds[0] <= bounds[1]) {
                    return (bounds[0] .. bounds[1])
                }
            }

            // Invalid range
            return IntRange.EMPTY
        }
    }

    data class RangedEnchantmentLevel(
        val enchantment: Enchantment,
        val levels: IntRange?
    )

    fun RangedEnchantmentLevel.matches(item: ItemStack): Boolean =
        item.containsEnchantment(enchantment) && (levels == null || item.getEnchantmentLevel(enchantment) in levels)
}
