package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterText : Filter<NoCompileData, Collection<String>>("text") {
    override val description = "Matches when the trigger text exactly matches one of the given values."
    override val categories = setOf("meta")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when no text is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val text = data.text ?: return true

        return value.containsIgnoreCase(text)
    }
}
