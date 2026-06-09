package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterIsNPC : Filter<NoCompileData, Boolean>("is_npc") {
    override val description = "Matches when the victim is (or is not) an NPC."
    override val categories = setOf("entity")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return true

        return value == victim.hasMetadata("NPC")
    }
}
