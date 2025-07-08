package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent

object FilterManaAbility : Filter<NoCompileData, Collection<String>>("mana_ability") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? ManaAbilityActivateEvent ?: return true

        return value.any { abilityName ->
            abilityName.equals(event.manaAbility.id.toString(), ignoreCase = true)
        }
    }
}