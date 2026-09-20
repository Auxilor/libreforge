package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpDemote : Trigger("lp_demote") {
    override val description = "Fires when the player is demoted along a LuckPerms track."

    override val categories = setOf("permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Only fires for players who are online on this server.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The group the player was demoted to."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
