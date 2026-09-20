package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.integrations.luckperms.trackName
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent

object FilterLpTrack : Filter<NoCompileData, Collection<String>>("lp_track") {
    override val description = "Matches when the LuckPerms track involved in the trigger is one of the given tracks."
    override val categories = setOf("permission")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Fails when the trigger has no LuckPerms track attached to it."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false
        val track = event.trackName ?: return false

        return value.containsIgnoreCase(track)
    }
}
