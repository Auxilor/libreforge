package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderText : TriggerPlaceholder("text") {
    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val text = data.text ?: return emptyList()

        return listOf(
            NamedValue(
                listOf("text", "string", "message"),
                text
            )
        )
    }
}
