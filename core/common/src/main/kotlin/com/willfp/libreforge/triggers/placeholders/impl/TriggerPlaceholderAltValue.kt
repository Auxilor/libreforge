package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderAltValue : TriggerPlaceholder("alt_trigger_value") {
    /**
     * The identifiers that resolve to the alt trigger value.
     */
    val identifiers = listOf("alt_trigger_value", "alttriggervalue", "alttrigger", "altvalue", "atv", "av", "at")

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        return listOf(
            NamedValue(
                identifiers,
                data.altValue
            )
        )
    }
}
