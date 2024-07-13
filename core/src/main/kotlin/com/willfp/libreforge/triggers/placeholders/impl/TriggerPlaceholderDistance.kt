package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderDistance : TriggerPlaceholder("distance") {

    private val placeholdersDistance = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.distance") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val victim = data.victim ?: return emptyList()
        val player = data.player ?: return emptyList()

        return listOfNotNull(
            if (placeholdersDistance.isNotEmpty()) NamedValue(placeholdersDistance, player.location.toVector().distance(victim.location.toVector())) else null
        )
    }
}
