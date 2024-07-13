package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderPlayer : TriggerPlaceholder("player") {

    private val placeholdersName = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.name") ?: emptyList()
    private val placeholdersUUID = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.uuid") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val player = data.player ?: return emptyList()

        return listOfNotNull(
            if (placeholdersName.isNotEmpty()) NamedValue(placeholdersName, player.name) else null,
            if (placeholdersUUID.isNotEmpty()) NamedValue(placeholdersUUID, player.uniqueId) else null
        )
    }
}
