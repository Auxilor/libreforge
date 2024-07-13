package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderLocation : TriggerPlaceholder("location") {

    private val placeholdersX = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.x") ?: emptyList()
    private val placeholdersY = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.y") ?: emptyList()
    private val placeholdersZ = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.z") ?: emptyList()
    private val placeholdersBlockX = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.blockX") ?: emptyList()
    private val placeholdersBlockY = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.blockY") ?: emptyList()
    private val placeholdersBlockZ = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.blockZ") ?: emptyList()
    private val placeholdersWorld = plugin.triggersPlaceholdersYml.getStringsOrNull("placeholders.${this.id}.aliases.world") ?: emptyList()

    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val location = data.location ?: return emptyList()

        return listOfNotNull(
            if (placeholdersX.isNotEmpty()) NamedValue(placeholdersX, location.x) else null,
            if (placeholdersBlockX.isNotEmpty()) NamedValue(placeholdersBlockX, location.blockX) else null,
            if (placeholdersY.isNotEmpty()) NamedValue(placeholdersY, location.y) else null,
            if (placeholdersBlockY.isNotEmpty()) NamedValue(placeholdersBlockY, location.blockY) else null,
            if (placeholdersZ.isNotEmpty()) NamedValue(placeholdersZ, location.z) else null,
            if (placeholdersBlockZ.isNotEmpty()) NamedValue(placeholdersBlockZ, location.blockZ) else null,
            if (placeholdersWorld.isNotEmpty()) NamedValue(placeholdersWorld, location.world?.name ?: "") else null
        )
    }
}
