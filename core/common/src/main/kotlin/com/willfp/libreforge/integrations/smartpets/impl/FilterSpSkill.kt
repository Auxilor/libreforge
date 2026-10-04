package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetSkillUnlockEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSpSkill : Filter<NoCompileData, Collection<String>>("sp_skill") {
    override val description = "Matches when the SmartPets skill that was unlocked is one of the given skill IDs."
    override val categories = setOf("pets")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a matching SmartPets event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PetSkillUnlockEvent ?: return true

        return value.containsIgnoreCase(event.skillId)
    }
}
