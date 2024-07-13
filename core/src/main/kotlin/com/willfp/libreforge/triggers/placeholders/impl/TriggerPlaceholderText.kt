package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderText : TriggerPlaceholder("text") {

    private val placeholdersText = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.text") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val text = data.text ?: return emptyList()

        return listOfNotNull(if (placeholdersText.isNotEmpty()) NamedValue(placeholdersText, text) else null)
    }
}
