package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsBuyPlot : Trigger("ps_buy_plot") {
    override val description = "Fires when the player buys a PlotSquared plot."

    override val categories = setOf("economy")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the bought plot.",
        TriggerParameter.VALUE to "The price paid for the plot."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )
}
