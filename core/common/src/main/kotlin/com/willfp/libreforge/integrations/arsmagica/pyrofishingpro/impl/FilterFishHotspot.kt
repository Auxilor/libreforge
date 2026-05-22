package com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import me.arsmagica.API.PyroFishCatchEvent

object FilterFishHotspot : Filter<NoCompileData, Boolean>("pyro_fish_hotspot") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val event = data.event as? PyroFishCatchEvent ?: return true
        return value == event.isHotspot
    }
}