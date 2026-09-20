package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsEnterPlot : Trigger("ps_enter_plot") {
    override val description = "Fires when the player enters a PlotSquared plot."

    override val categories = setOf("movement")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the entered plot."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
