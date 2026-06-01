package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EffectRemoveEnchant : Effect<NoCompileData>("remove_enchant") {
    override val description = "Removes a specific enchantment from the triggering item."
    override val categories = setOf("inventory")

    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override val arguments = arguments {
        require(
            "enchant",
            "You must specify the enchantment to remove!",
            description = "The enchantment to remove from the item.",
            type = ArgType.ENCHANTMENT
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val meta = item.itemMeta ?: return false

        val enchant = getEnchantment(config.getString("enchant")) ?: return false

        if (meta is EnchantmentStorageMeta) {
            meta.removeStoredEnchant(enchant)
        } else {
            meta.removeEnchant(enchant)
        }

        item.itemMeta = meta

        return true
    }
}
