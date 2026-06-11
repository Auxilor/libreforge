package com.willfp.libreforge.integrations.pyrofishingpro.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import me.arsmagica.API.PyroFishCatchEvent

object FilterFishTier : Filter<NoCompileData, Collection<String>>("pyro_fish_tier") {
    override val description = "Matches when the caught fish tier matches one of the given tier names."
    override val categories = setOf("player")
    override val valueType = ArgType.STRING_LIST

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PyroFishCatchEvent ?: return false
        return value.any { it.equals(event.tier, ignoreCase = true) }
    }
}