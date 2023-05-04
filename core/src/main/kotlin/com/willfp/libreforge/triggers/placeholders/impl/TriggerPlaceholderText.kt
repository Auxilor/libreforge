package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderText : TriggerPlaceholder("text") {
    override fun createPlaceholders(trigger: DispatchedTrigger): Collection<NamedValue> {
        return listOf(
            NamedValue(
                listOf("text", "string", "message"),
                trigger.data.text ?: ""
            )
        )
    }
}
