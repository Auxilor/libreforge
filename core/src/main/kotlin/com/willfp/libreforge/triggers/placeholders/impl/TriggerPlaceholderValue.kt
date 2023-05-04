package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderValue : TriggerPlaceholder("trigger_value") {
    override fun createPlaceholders(trigger: DispatchedTrigger): Collection<NamedValue> {
        return listOf(
            NamedValue(
                listOf("trigger_value", "triggervalue", "trigger", "value", "tv", "v", "t"),
                trigger.data.value
            )
        )
    }
}
