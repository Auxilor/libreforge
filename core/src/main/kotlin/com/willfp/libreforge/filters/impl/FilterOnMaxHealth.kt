package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.attribute.Attribute

object FilterOnMaxHealth : Filter<NoCompileData, Boolean>("on_max_health") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun filter(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true

        val maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value
        val onMaxHealth = entity.health == maxHealth

        return onMaxHealth == value
    }
}
