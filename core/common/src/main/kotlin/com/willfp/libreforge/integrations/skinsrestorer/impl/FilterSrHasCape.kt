package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.skinsrestorer.hasCape
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.triggers.TriggerData

object FilterSrHasCape : Filter<NoCompileData, Boolean>("sr_has_cape") {
    override val description = "Matches when the player's skin does (or does not) include a cape."

    override val categories = setOf("player")

    override val valueType = ArgType.BOOLEAN

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Only reads the cape stored on the SkinsRestorer skin, so it does not see capes applied by other plugins.",
        "Passes automatically when there is no player in the trigger data."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val player = data.player ?: return true

        val hasCape = player.lookupSkin().property?.hasCape() ?: false

        return value == hasCape
    }
}
