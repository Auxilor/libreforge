package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.attribute.Attribute

object FilterOnMaxHealth : Filter<NoCompileData, Boolean>("on_max_health") {
    override val description = "Matches when the victim is (or is not) at maximum health."
    override val categories = setOf("entity")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true

        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value
        val onMaxHealth = entity.health == maxHealth

        return onMaxHealth == value
    }
}
