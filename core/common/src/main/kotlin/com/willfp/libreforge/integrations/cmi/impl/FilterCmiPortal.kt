package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.events.CMIPortalUseEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterCmiPortal : Filter<NoCompileData, List<String>>("cmi_portal") {
    override val description = "Matches when the player used one of the given CMI portals."
    override val categories = setOf("player", "world")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Passes automatically when the event is not a CMI portal event."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): List<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: List<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? CMIPortalUseEvent ?: return true

        return value.containsIgnoreCase(event.portal?.name ?: return false)
    }
}
