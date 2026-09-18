package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsTrustedOnPlot : Trigger("ps_trusted_on_plot") {
    override val description = "Fires when the player is trusted on a PlotSquared plot."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Also fires when a merge copies trusted players onto the merged plot.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the plot the player was trusted on.",
        TriggerParameter.VICTIM to "The player who trusted them, if it was a player."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT,
        TriggerParameter.VICTIM
    )
}
