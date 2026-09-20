package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.integrations.luckperms.getTrack
import com.willfp.libreforge.integrations.luckperms.lpUser
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpValueToTrackPosition : Mutator<NoCompileData>("lp_value_to_track_position") {
    override val description = "Sets the value to the position of the primary group of the player in a LuckPerms track."

    override val categories = setOf("value", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Positions start at 0, which is the first group in the track.",
        "No-ops if the track is not loaded or if the primary group is not in the track."
    )

    override val arguments = arguments {
        require(
            "track",
            "You must specify the track!",
            description = "The name of the LuckPerms track.",
            type = ArgType.STRING,
            example = "ranks"
        )
    }

    override val parameterTransformers = parameterTransformers {
        adds(TriggerParameter.VALUE)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val user = data.player?.lpUser ?: return data
        val track = getTrack(config.getString("track")) ?: return data

        val position = track.groups.indexOfFirst { it.equals(user.primaryGroup, ignoreCase = true) }

        if (position < 0) {
            return data
        }

        return data.copy(
            value = position.toDouble()
        )
    }
}
