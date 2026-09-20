package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpNodeRemove : Trigger("lp_node_remove") {
    override val description = "Fires when any LuckPerms node is removed from the player."

    override val categories = setOf("permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Only fires for players who are online on this server.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The raw key of the node that was removed."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION,
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
