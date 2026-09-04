package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPlayerItemsSellEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterCmiSellType : Filter<NoCompileData, List<String>>("cmi_sell_type") {
    override val description = "Matches when the items were sold through one of the given CMI sell types."
    override val categories = setOf("economy", "player")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Passes automatically when the event is not a CMI item sell event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? CMIPlayerItemsSellEvent ?: return true

        return value.containsIgnoreCase(event.sellType?.name ?: return false)
    }
}
