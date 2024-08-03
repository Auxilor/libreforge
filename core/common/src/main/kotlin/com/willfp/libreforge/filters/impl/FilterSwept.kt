package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.entity.EntityDamageEvent

object FilterSwept : Filter<NoCompileData, Boolean>("swept") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val cause = (data.event as? EntityDamageEvent)?.cause ?: return true
        return cause == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK == value
    }
}
