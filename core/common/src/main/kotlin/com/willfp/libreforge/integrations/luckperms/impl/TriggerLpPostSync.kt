package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpPostSync : Trigger("lp_post_sync") {
    override val description = "Fires after LuckPerms has finished syncing data from storage."

    override val categories = setOf("permission", "meta")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Is not tied to a player, so it cannot be used with effects that require one.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched."
    )

    override val parameters = setOf(
        TriggerParameter.EVENT
    )
}
