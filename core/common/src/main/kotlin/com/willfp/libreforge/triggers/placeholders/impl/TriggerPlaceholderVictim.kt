package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder
import org.bukkit.attribute.Attribute

object TriggerPlaceholderVictim : TriggerPlaceholder("victim") {

    private val placeholdersVictimHealth = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.health") ?: emptyList()
    private val placeholdersVictimMaxHealth = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.max_health") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val victim = data.victim ?: return emptyList()

        return listOfNotNull(
            if (placeholdersVictimHealth.isNotEmpty()) NamedValue(placeholdersVictimHealth, victim.health) else null,
            if (placeholdersVictimMaxHealth.isNotEmpty()) NamedValue(placeholdersVictimMaxHealth, victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0) else null
        )
    }
}
