package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.entity.EntityDamageEvent

object FilterDamageCause : Filter<NoCompileData, Collection<String>>("damage_cause") {
    override val description = "Matches when the damage cause matches one of the given causes."
    override val categories = setOf("combat")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a damage event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val cause = (data.event as? EntityDamageEvent)?.cause ?: return true
        return value.containsIgnoreCase(cause.name)
    }
}
