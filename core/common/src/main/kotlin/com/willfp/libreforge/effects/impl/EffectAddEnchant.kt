package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EffectAddEnchant : Effect<NoCompileData>("add_enchant") {
    override val description = "Adds an enchantment to the triggering item."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "enchant",
            "You must specify the enchantment to add!",
            description = "The enchantment to add, e.g. sharpness.",
            type = ArgType.ENCHANTMENT
        )
        require(
            "level",
            "You must specify the level of the enchantment to add!",
            description = "The level of the enchantment to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level% + 1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false
        val meta = item.itemMeta ?: return false

        val enchant = getEnchantment(config.getString("enchant")) ?: return false
        val level = config.getIntFromExpression("level", data)

        if (meta is EnchantmentStorageMeta) {
            meta.addStoredEnchant(enchant, level, true)
        } else {
            meta.addEnchant(enchant, level, true)
        }

        item.itemMeta = meta

        return true
    }
}
