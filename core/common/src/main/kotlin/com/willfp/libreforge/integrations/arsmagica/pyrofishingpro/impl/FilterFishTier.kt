package com.willfp.libreforge.integrations.arsmagica.pyrofishingpro.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import me.arsmagica.API.PyroFishCatchEvent

object FilterFishTier : Filter<NoCompileData, Collection<String>>("pyro_fish_tier") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PyroFishCatchEvent ?: return false
        return value.any { it.equals(event.tier, ignoreCase = true) }
    }
}