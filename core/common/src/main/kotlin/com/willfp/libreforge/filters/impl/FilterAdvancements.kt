package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.tmmobcoins.lib.CBA.CBAMethods.name
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object FilterAdvancements : Filter<NoCompileData, Collection<String>>("advancements") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as? PlayerAdvancementDoneEvent ?: return true

        return value.containsIgnoreCase(event.advancement.key.toString())
    }
}
