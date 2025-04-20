package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterMcMMOAbility : Filter<NoCompileData, Collection<String>>("mcmmo_ability") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? McMMOPlayerAbilityEvent ?: return true

        return value.contains(event.ability.name)
    }
}