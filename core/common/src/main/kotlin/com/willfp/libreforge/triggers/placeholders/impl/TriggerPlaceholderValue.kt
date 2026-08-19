package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderValue : TriggerPlaceholder("trigger_value") {
    /**
     * The identifiers that resolve to the trigger value.
     */
    val identifiers = listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t")

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        return listOf(
            NamedValue(
                identifiers,
                data.value
            )
        )
    }
}
