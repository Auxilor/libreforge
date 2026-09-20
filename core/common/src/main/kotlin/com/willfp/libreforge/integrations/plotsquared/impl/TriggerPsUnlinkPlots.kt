package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerPsUnlinkPlots : Trigger("ps_unlink_plots") {
    override val description = "Fires when a PlotSquared plot owned by the player is unlinked."

    override val categories = setOf("world")

    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Fires for any unlink reason, including when a plot is cleared, deleted or given a new owner.",
        "Only fires for players who are online on this server."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The ID of the unlinked plot."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
