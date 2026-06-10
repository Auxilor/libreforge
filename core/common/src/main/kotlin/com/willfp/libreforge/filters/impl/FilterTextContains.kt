package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterTextContains : Filter<NoCompileData, Collection<String>>("text_contains") {
    override val description = "Matches when the trigger text contains one of the given substrings."
    override val categories = setOf("meta")
    override val valueType = ArgType.STRING_LIST
    override val additionalInfo = listOf("Passes automatically when no text is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val text = data.text ?: return true

        val lowerText = text.lowercase()
        return value.any { test -> lowerText.contains(test.lowercase()) }
    }
}
