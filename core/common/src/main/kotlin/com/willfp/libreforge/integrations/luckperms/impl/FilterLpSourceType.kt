package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.integrations.luckperms.sourceTypeName
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent

object FilterLpSourceType : Filter<NoCompileData, Collection<String>>("lp_source_type") {
    override val description = "Matches when the LuckPerms change came from one of the given sources."
    override val categories = setOf("permission")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Valid sources are ENTITY, meaning a player or the console ran the change, and UNKNOWN, meaning the API did.",
        "Filtering for ENTITY stops effects from reacting to changes they made themselves.",
        "Fails when the trigger has no source attached to it."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false
        val source = event.sourceTypeName ?: return false

        return value.containsIgnoreCase(source)
    }
}
