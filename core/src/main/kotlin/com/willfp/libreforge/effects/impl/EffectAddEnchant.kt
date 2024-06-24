package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getEnchantment
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EffectAddEnchant : Effect<NoCompileData>("add_enchant") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

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
