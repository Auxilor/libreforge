package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.integrations.luckperms.getTrack
import com.willfp.libreforge.integrations.luckperms.lpUser
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLpTextToTrackPreviousGroup : Mutator<NoCompileData>("lp_text_to_track_previous_group") {
    override val description = "Sets the text to the group below the primary group of the player in a LuckPerms track."

    override val categories = setOf("chat", "permission")

    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "No-ops if the track is not loaded, or if the player is already in the first group of the track."
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
        adds(TriggerParameter.TEXT)
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val user = data.player?.lpUser ?: return data
        val track = getTrack(config.getString("track")) ?: return data
        val group = LuckPermsManager.luckPerms?.groupManager?.getGroup(user.primaryGroup) ?: return data

        return data.copy(
            text = track.getPrevious(group) ?: return data
        )
    }
}
