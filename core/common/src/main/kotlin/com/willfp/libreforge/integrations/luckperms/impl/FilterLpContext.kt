package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.integrations.luckperms.mutatedNode
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent

object FilterLpContext : Filter<NoCompileData, Collection<String>>("lp_context") {
    override val description = "Matches when the LuckPerms node in the trigger was set in one of the given contexts."
    override val categories = setOf("permission")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Contexts are written as key=value entries, for example server=survival.",
        "Fails when the trigger has no LuckPerms node attached to it."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false
        val contexts = event.mutatedNode?.contexts ?: return false

        return value.any {
            val split = it.split("=", limit = 2)

            if (split.size != 2) {
                false
            } else {
                contexts.contains(split[0].trim(), split[1].trim())
            }
        }
    }
}
