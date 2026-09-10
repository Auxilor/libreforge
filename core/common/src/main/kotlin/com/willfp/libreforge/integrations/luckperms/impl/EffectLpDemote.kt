package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.luckperms.getContexts
import com.willfp.libreforge.integrations.luckperms.getTrack
import com.willfp.libreforge.integrations.luckperms.modifyUser
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectLpDemote : Effect<NoCompileData>("lp_demote") {
    override val description = "Demotes the player along a LuckPerms track."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Saves asynchronously, so the change is not visible immediately.",
        "Does nothing if the track is not loaded, or if the player is already in the first group of the track."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "track",
            "You must specify the track!",
            description = "The name of the LuckPerms track to demote along.",
            type = ArgType.STRING,
            example = "ranks"
        )
        optional(
            "contexts",
            description = "The LuckPerms contexts to demote in, as a list of key=value entries.",
            type = ArgType.STRING_LIST,
            default = "[]",
            example = listOf("server=survival")
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val track = getTrack(config.getString("track")) ?: return false
        val contexts = config.getContexts("contexts")

        return modifyUser(player) { user ->
            track.demote(user, contexts)
        }
    }
}
