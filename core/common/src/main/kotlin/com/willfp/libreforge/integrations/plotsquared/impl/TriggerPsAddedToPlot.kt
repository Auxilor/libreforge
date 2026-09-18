package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsAddedToPlot : Trigger("ps_added_to_plot") {
    override val description = "Fires when the player is added as a member of a PlotSquared plot."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Also fires when a merge copies members onto the merged plot.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the plot the player was added to.",
        TriggerParameter.VICTIM to "The player who added them, if it was a player."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT,
        TriggerParameter.VICTIM
    )
}
