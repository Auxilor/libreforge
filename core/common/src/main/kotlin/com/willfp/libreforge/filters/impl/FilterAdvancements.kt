package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object FilterAdvancements : Filter<NoCompileData, Collection<String>>("advancements") {
    override val description = "Matches when the advancement completed matches one of the given advancement keys."
    override val categories = setOf("player")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when the event is not an advancement event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerAdvancementDoneEvent ?: return true

        return value.containsIgnoreCase(event.advancement.key.toString())
    }
}
