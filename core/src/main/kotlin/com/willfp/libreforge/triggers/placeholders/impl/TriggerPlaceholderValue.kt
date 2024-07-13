package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderValue : TriggerPlaceholder("trigger_value") {

    private val placeholdersTriggerValue = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.value") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        return listOfNotNull(if (placeholdersTriggerValue.isNotEmpty()) NamedValue(placeholdersTriggerValue, data.value) else null)
    }
}
