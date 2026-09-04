package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIUserKitAcquireEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterCmiKit : Filter<NoCompileData, List<String>>("cmi_kit") {
    override val description = "Matches when the player claimed one of the given CMI kits."
    override val categories = setOf("player")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Passes automatically when the event is not a CMI kit event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? CMIUserKitAcquireEvent ?: return true

        return value.containsIgnoreCase(event.kit?.configName ?: return false)
    }
}
