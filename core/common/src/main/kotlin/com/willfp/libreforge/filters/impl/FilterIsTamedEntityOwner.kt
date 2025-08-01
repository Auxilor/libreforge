package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Tameable

object FilterIsTamedEntityOwner : Filter<NoCompileData, Boolean>("is_tamed_entity_owner") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val victim = data.victim
        val damager = data.player

        if (victim !is Tameable || damager == null) return !value

        return (victim.owner?.uniqueId == damager.uniqueId) == value
    }
}
