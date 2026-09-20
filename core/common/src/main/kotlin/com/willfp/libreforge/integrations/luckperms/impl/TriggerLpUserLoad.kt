package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpUserLoad : Trigger("lp_user_load") {
    override val description = "Fires when LuckPerms loads the data of the player."

    override val categories = setOf("permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Only fires for players who are online on this server.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched."
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
