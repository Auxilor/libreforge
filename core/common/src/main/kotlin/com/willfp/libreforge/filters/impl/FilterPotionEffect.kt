package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.entity.EntityPotionEffectEvent

object FilterPotionEffect : Filter<NoCompileData, Collection<String>>("potion_effect") {
    override val description = "Matches when the potion effect being applied matches one of the given effect types."
    override val categories = setOf("player", "entity")
    override val valueType = ArgType.POTION_EFFECT_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not a potion effect event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? EntityPotionEffectEvent ?: return true

        return value.containsIgnoreCase(event.newEffect?.type?.key?.key ?: "")
    }
}
