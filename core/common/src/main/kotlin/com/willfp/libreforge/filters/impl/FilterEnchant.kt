package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.enchantment.EnchantItemEvent

object FilterEnchant : Filter<NoCompileData, Collection<String>>("enchant") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key).map(String::uppercase)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? EnchantItemEvent ?: return true
        val enchants = event.enchantsToAdd.keys.map { it.key.key.uppercase() }
        return value.any { it in enchants }
    }
}
