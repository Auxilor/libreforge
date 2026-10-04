package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetAdoptEvent
import com.smartpets.events.PetEvolveEvent
import com.smartpets.events.PetRemoveEvent
import com.smartpets.events.PetSkillUnlockEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterSpCause : Filter<NoCompileData, Collection<String>>("sp_cause") {
    override val description = "Matches when the cause of the SmartPets adopt, remove, evolve or skill unlock is one of the given causes."
    override val categories = setOf("pets")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Adopt causes: SHOP, ADMIN, PLUGIN.",
        "Remove causes: RELEASE, ADMIN_DELETE, ADMIN_RESET, PLUGIN.",
        "Evolve causes: PLAYER, PLUGIN.",
        "Skill unlock causes: LEVEL_UP, EVOLUTION, PLUGIN.",
        "Passes automatically when the event has no SmartPets cause."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val cause = when (val event = data.event) {
            is PetAdoptEvent -> event.cause.name
            is PetRemoveEvent -> event.cause.name
            is PetEvolveEvent -> event.cause.name
            is PetSkillUnlockEvent -> event.cause.name
            else -> return true
        }

        return value.containsIgnoreCase(cause)
    }
}
