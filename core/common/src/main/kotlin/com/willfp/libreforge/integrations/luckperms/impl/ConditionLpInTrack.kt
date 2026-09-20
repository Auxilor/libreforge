package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.getTrack
import com.willfp.libreforge.integrations.luckperms.lpUser
import org.bukkit.entity.Player

object ConditionLpInTrack : Condition<NoCompileData>("lp_in_track") {
    override val description = "Passes when the primary group of the player is part of the specified LuckPerms track."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Fails if the track is not loaded."
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

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.lpUser ?: return false
        val track = getTrack(config.getString("track")) ?: return false

        return track.containsGroup(user.primaryGroup)
    }
}
