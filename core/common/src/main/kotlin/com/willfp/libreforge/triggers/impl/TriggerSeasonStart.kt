package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `<season>_start`, generated for every season in seasons.yml and fired by [com.willfp.libreforge.seasons.Seasons].
 */
class TriggerSeasonStart(
    seasonId: String
) : Trigger("${seasonId}_start") {
    override val description = "Fires for every online player at midnight when ${seasonId.replace('_', ' ')} begins."

    override val categories = setOf("season")

    override val additionalInfo = listOf(
        "Midnight is in the timezone set by dates.timezone in config.yml.",
        "Players who join later in the season are not triggered; use the is_$seasonId condition for that.",
        "Seasons are defined in seasons.yml."
    )

    override val parameterDescriptions = mapOf(
        TriggerParameter.LOCATION to "The player's location."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )
}
