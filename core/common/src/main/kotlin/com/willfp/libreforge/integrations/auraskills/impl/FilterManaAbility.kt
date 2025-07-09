package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent
import dev.aurelium.auraskills.api.mana.ManaAbility
import dev.aurelium.auraskills.api.registry.NamespacedId

object FilterManaAbility : Filter<NoCompileData, Collection<ManaAbility>>("mana_ability") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<ManaAbility> {
        return config.getStrings(key).mapNotNull { abilityId ->
            try {
                AuraSkillsApi.get().globalRegistry.getManaAbility(NamespacedId.fromDefault(abilityId))
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    override fun isMet(data: TriggerData, value: Collection<ManaAbility>, compileData: NoCompileData): Boolean {
        val event = data.event as? ManaAbilityActivateEvent ?: return true
        return value.any { it == event.manaAbility }
    }
}