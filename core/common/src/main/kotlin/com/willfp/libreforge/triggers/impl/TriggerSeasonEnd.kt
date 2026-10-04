package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerParameter

/**
 * `<season>_end`, generated for every season in seasons.yml and fired by [com.willfp.libreforge.seasons.Seasons].
 */
class TriggerSeasonEnd(
    seasonId: String
) : Trigger("${seasonId}_end") {
    override val description = "Fires for every online player in the last minute (23:59) of ${seasonId.replace('_', ' ')}."

    override val categories = setOf("season")

    override val additionalInfo = listOf(
        "23:59 is in the timezone set by dates.timezone in config.yml.",
        "Only players online at that moment are triggered.",
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
