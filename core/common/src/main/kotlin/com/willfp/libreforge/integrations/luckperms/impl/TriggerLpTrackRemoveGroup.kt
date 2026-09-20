package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

object TriggerLpTrackRemoveGroup : Trigger("lp_track_remove_group") {
    override val description = "Fires when a group is removed from a LuckPerms track."

    override val categories = setOf("permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Is not tied to a player, so it cannot be used with effects that require one.",
        "LuckPerms fires this event asynchronously, so it is passed to the main thread before being dispatched."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.TEXT to "The name of the group that was removed."
    )

    override val parameters = setOf(
        TriggerParameter.TEXT,
        TriggerParameter.EVENT
    )
}
