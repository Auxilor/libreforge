package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpCustomMessage : Trigger("lp_custom_message") {
    override val description = "Fires when a custom LuckPerms message is received, including from other servers on the network."

    override val categories = setOf("permission", "meta")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Is not tied to a player, so it cannot be used with effects that require one.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched.",
        "Pairs with the lp_send_custom_message effect to run effects across a network."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The payload of the message."
    )

    override val parameters = setOf(
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
