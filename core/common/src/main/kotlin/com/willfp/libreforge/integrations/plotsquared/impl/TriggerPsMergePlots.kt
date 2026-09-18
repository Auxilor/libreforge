package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsMergePlots : Trigger("ps_merge_plots") {
    override val description = "Fires when the player merges PlotSquared plots."

    override val categories = setOf("world")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the merged plot.",
        TriggerParameter.VALUE to "The number of plots now connected by the merge."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )
}
