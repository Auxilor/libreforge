package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.hologram.HologramManager
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedStrings
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCreateHologram : Effect<NoCompileData>("create_hologram") {
    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require("text", "You must specify the text to display!")
        require("duration", "You must specify the duration to display for!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false

        val duration = config.getDoubleFromExpression("duration", data)
        val text = config.getFormattedStrings("text", data)

        val hologram = HologramManager.createHologram(location, text)

        plugin.scheduler.runLater(duration.toLong()) {
            hologram.remove()
        }

        return true
    }
}
