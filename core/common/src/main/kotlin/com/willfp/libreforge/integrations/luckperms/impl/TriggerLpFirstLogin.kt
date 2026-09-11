package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpFirstLogin : Trigger("lp_first_login") {
    override val description = "Fires when the player joins the server for the first time, as tracked by LuckPerms."

    override val categories = setOf("permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Only fires for players who are online on this server.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched.",
        "LuckPerms fires this before the player has fully joined, so it is dispatched one second later, once they are online."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The username of the player."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
