package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.messaging.CustomMessageReceiveEvent

object FilterLpChannel : Filter<NoCompileData, Collection<String>>("lp_channel") {
    override val description = "Matches when a custom LuckPerms message was sent on one of the given channels."
    override val categories = setOf("permission", "meta")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Only applies to the lp_custom_message trigger.",
        "Fails when the trigger is not a custom message."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<CustomMessageReceiveEvent>() ?: return false

        return value.containsIgnoreCase(event.channelId)
    }
}
