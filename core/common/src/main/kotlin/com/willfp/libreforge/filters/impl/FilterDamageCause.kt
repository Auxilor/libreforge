package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import io.lumine.mythic.bukkit.events.MythicDamageEvent
import org.bukkit.event.entity.EntityDamageEvent

object FilterDamageCause : Filter<NoCompileData, Collection<String>>("damage_cause") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val cause = when {
            data.event is EntityDamageEvent -> data.event.cause
            data.event is MythicDamageEvent -> data.event.damageMetadata.damageCause
            else -> null
        }
        return cause?.let { value.containsIgnoreCase(it.name) } ?: true
    }
}
