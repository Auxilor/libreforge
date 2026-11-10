package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta

object EffectAddEnchantLevel : Effect<NoCompileData>("add_enchant_level") {
    override val description = "Adds levels to an enchantment on the triggering item, removing it at 0."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            name = "enchant",
            message = "You must specify the enchantment to level!",
            description = "The enchantment to level, e.g. sharpness.",
            type = ArgType.ENCHANTMENT
        )
        require(
            name = "levels",
            message = "You must specify the levels to add!",
            description = "The levels to add. Use a negative number to remove levels. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1"
        )
        optional(
            "allow_unsafe",
            description = "If true, the enchantment can go above its max level.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem
        val meta = item?.itemMeta
        val enchant = getEnchantment(config.getFormattedString("enchant", data))
        val current = enchant?.let { meta?.levelOf(it) } ?: 0

        if (meta == null || enchant == null || current <= 0) {
            return false
        }

        val target = current + config.getIntFromExpression("levels", data)
        val level = if (config.getBool("allow_unsafe")) {
            target
        } else {
            target.coerceAtMost(enchant.maxLevel.coerceAtLeast(current))
        }

        meta.setLevel(enchant, level)
        item.itemMeta = meta

        return true
    }

    private fun ItemMeta.levelOf(enchant: Enchantment): Int =
        if (this is EnchantmentStorageMeta) getStoredEnchantLevel(enchant) else getEnchantLevel(enchant)

    private fun ItemMeta.setLevel(enchant: Enchantment, level: Int) {
        when {
            this is EnchantmentStorageMeta && level > 0 -> addStoredEnchant(enchant, level, true)
            this is EnchantmentStorageMeta -> removeStoredEnchant(enchant)
            level > 0 -> addEnchant(enchant, level, true)
            else -> removeEnchant(enchant)
        }
    }
}
