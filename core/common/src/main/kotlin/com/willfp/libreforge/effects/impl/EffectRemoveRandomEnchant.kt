package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EffectRemoveRandomEnchant : Effect<NoCompileData>("remove_random_enchant") {
    override val parameters = setOf(
        TriggerParameter.ITEM
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return false

        val meta = item.itemMeta ?: return false

        val enchants = if (meta is EnchantmentStorageMeta) {
            meta.storedEnchants
        } else {
            meta.enchants
        }

        if (enchants.isEmpty()) {
            return false
        }

        val (enchant, level) = enchants.entries.random()

        val mode = config.getStringOrNull("mode")?.lowercase() ?: "full"

        if (mode == "level" && level > 1) {
            if (meta is EnchantmentStorageMeta) {
                meta.removeStoredEnchant(enchant)
                meta.addStoredEnchant(enchant, level - 1, true)
            } else {
                meta.removeEnchant(enchant)
                meta.addEnchant(enchant, level - 1, true)
            }
        } else {
            if (meta is EnchantmentStorageMeta) {
                meta.removeStoredEnchant(enchant)
            } else {
                meta.removeEnchant(enchant)
            }
        }

        item.itemMeta = meta

        return true
    }
}