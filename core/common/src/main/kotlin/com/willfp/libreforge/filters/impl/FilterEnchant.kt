package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.enchantment.EnchantItemEvent

object FilterEnchant : Filter<NoCompileData, Collection<String>>("enchant") {
    override val description = "Matches when one of the enchantments being applied matches one of the given enchantment IDs."
    override val categories = setOf("inventory")
    override val valueType = ArgType.ENCHANTMENT_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not an enchanting event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key).map(String::uppercase)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? EnchantItemEvent ?: return true
        val enchants = event.enchantsToAdd.keys.map { it.key.key.uppercase() }
        return value.any { it in enchants }
    }
}
