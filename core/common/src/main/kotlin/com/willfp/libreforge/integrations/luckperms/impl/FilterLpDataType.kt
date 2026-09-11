package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.luckperms.asLuckPermsEvent
import com.willfp.libreforge.integrations.luckperms.dataTypeName
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.event.LuckPermsEvent
import net.luckperms.api.model.data.DataType

object FilterLpDataType : Filter<NoCompileData, Collection<String>>("lp_data_type") {
    override val description = "Matches when the LuckPerms data that changed is of one of the given types."
    override val categories = setOf("permission")
    override val valueType = ArgType.STRING_LIST
    override val valueEnumClass = DataType::class
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "NORMAL is persistent data, TRANSIENT is data that is lost on server restart.",
        "Fails when the trigger did not change any LuckPerms data."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event.asLuckPermsEvent<LuckPermsEvent>() ?: return false
        val dataType = event.dataTypeName ?: return false

        return value.containsIgnoreCase(dataType)
    }
}
