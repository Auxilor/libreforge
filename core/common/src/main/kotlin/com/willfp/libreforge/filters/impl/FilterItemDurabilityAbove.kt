package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.inventory.meta.Damageable

object FilterItemDurabilityAbove : Filter<NoCompileData, Int>("item_durability_above") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Int {
        return config.getIntFromExpression(key, data)
    }

    override fun isMet(data: TriggerData, value: Int, compileData: NoCompileData): Boolean {
        val item = data.foundItem ?: return true
        val meta = item.itemMeta as? Damageable ?: return true

        return (item.type.maxDurability - meta.damage) >= value
    }
}
