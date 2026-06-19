package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.meta.Damageable

object FilterItemDurabilityBelowPercent : Filter<NoCompileData, Double>("item_durability_below_percent") {
    override val description = "Matches when the held item's remaining durability percentage is at or below the given value."
    override val categories = setOf("inventory")
    override val valueType = ArgType.DOUBLE
    override val additionalInfo = listOf("Passes automatically when no item is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Double {
        return config.getDoubleFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Double, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return true
        val meta = item.itemMeta as? Damageable ?: return true

        return (item.type.maxDurability - meta.damage) / item.type.maxDurability <= (value / 100.0)
    }
}
