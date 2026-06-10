package com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import me.arsmagica.API.PyroFishCatchEvent

object FilterFishHotspot : Filter<NoCompileData, Boolean>("pyro_fish_hotspot") {
    override val description = "Matches when the fishing catch is (or is not) from a hotspot."
    override val categories = setOf("player")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when the event is not a PyroFishingPro catch event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? PyroFishCatchEvent ?: return true
        return value == event.isHotspot
    }
}