package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent
import net.luckperms.api.event.group.GroupCreateEvent
import net.luckperms.api.event.group.GroupDeleteEvent
import net.luckperms.api.event.track.TrackCreateEvent
import net.luckperms.api.event.track.TrackDeleteEvent

object FilterLpCause : Filter<NoCompileData, Collection<String>>("lp_cause") {
    override val description = "Matches when a LuckPerms creation or deletion was caused in one of the given ways."
    override val categories = setOf("permission")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Valid causes are COMMAND, WEB_EDITOR, API and INTERNAL.",
        "Fails when the trigger is not a creation or deletion."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false

        val cause = when (event) {
            is GroupCreateEvent -> event.cause.name
            is GroupDeleteEvent -> event.cause.name
            is TrackCreateEvent -> event.cause.name
            is TrackDeleteEvent -> event.cause.name
            else -> return false
        }

        return value.containsIgnoreCase(cause)
    }
}
