package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
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
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.PatternSyntaxException

object ConditionHasEnchant : Condition<NoCompileData>("has_enchant") {
    override val description = "Passes when the entity has an item with the specified enchantment in the specified slot."
    override val categories = setOf("inventory")
    override val additionalInfo = listOf(
        "Enchantment levels can be specified as a single integer or a range (e.g. sharpness:3-5).",
        "Entries starting with regex: match any enchantment whose ID (sharpness or minecraft:sharpness) fully matches the pattern, with an optional level or range at the end (e.g. regex:.*_curse or regex:(sharpness|smite):3-5)."
    )

    private val levelSuffix = Regex("\\d+(-\\d+)?")

    private val patterns = ConcurrentHashMap<String, Regex>()

    private val matchesNothing = Regex("(?!)")

    override val arguments = arguments {
        require(
            listOf("slot", "slots"),
            "You must specify the slot(s)!",
            description = "The equipment slot(s) to check for the enchantment.",
            type = ArgType.STRING_LIST,
            example = listOf("mainhand", "9")
        )
        require(
            listOf("enchant", "enchants"),
            "You must specify the enchant(s)!",
            description = "The enchantment(s) to require, optionally with a level or range (e.g. sharpness:3-5), or regex: patterns.",
            type = ArgType.ENCHANTMENT_LIST,
            example = listOf("sharpness:3-5", "unbreaking:2", "regex:.*_curse")
        )
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
        return slotTypes.flatMap { it.getItems(entity) }
    }

    private fun parseSlotTypes(config: Config) : List<SlotType> {
        val slots = config.getStrings("slots", "slot")

        return slots.mapNotNull { SlotTypes[it] }
    }

    private fun parseEnchants(config: Config) : List<EnchantmentMatcher> {
        val enchants = config.getStrings("enchants", "enchant")

        return enchants.mapNotNull { key ->
            if (key.startsWith("regex:")) {
                return@mapNotNull parseRegex(key.removePrefix("regex:"))
            }

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

    private fun parseRegex(entry: String): RegexEnchantmentLevel {
        val levels = entry.substringAfterLast(delimiter = ":", missingDelimiterValue = "").takeIf { levelSuffix.matches(it) }
        val pattern = if (levels == null) entry else entry.substringBeforeLast(":")

        val regex = try {
            patterns.getOrPut(pattern) { Regex(pattern) }
        } catch (_: PatternSyntaxException) {
            matchesNothing
        }

        return RegexEnchantmentLevel(regex, levels?.let { parseLevelRange(it) })
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

    sealed interface EnchantmentMatcher {
        fun matches(item: ItemStack): Boolean
    }

    data class RangedEnchantmentLevel(
        val enchantment: Enchantment,
        val levels: IntRange?
    ) : EnchantmentMatcher {
        override fun matches(item: ItemStack): Boolean =
            item.containsEnchantment(enchantment) && (levels == null || item.getEnchantmentLevel(enchantment) in levels)
    }

    data class RegexEnchantmentLevel(
        val regex: Regex,
        val levels: IntRange?
    ) : EnchantmentMatcher {
        override fun matches(item: ItemStack): Boolean =
            item.enchantments.any { (enchantment, level) ->
                (regex.matches(enchantment.key.key) || regex.matches(enchantment.key.toString())) &&
                    (levels == null || level in levels)
            }
    }
}
