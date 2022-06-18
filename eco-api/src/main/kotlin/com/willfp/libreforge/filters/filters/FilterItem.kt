package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData

class FilterItem : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val item = data.item ?: return true

        return config.withInverse("item", Config::getStringsOrNull) {
            it?.map { lookup -> Items.lookup(lookup) }?.any { testableItem ->
                testableItem.matches(item)
            } == true
        }
    }
}
