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

object ConditionLpTrackPositionAbove : Condition<NoCompileData>("lp_track_position_above") {
    override val description =
        "Passes when the primary group of the player is at or above the specified position in a LuckPerms track."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Positions start at 0, which is the first group in the track.",
        "Fails if the track is not loaded or if the primary group is not in the track."
    )

    override val arguments = arguments {
        require(
            "track",
            "You must specify the track!",
            description = "The name of the LuckPerms track.",
            type = ArgType.STRING,
            example = "ranks"
        )
        require(
            "position",
            "You must specify the minimum position!",
            description = "The minimum position in the track.",
            type = ArgType.EXPRESSION,
            example = "2"
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

        val position = track.groups.indexOfFirst { it.equals(user.primaryGroup, ignoreCase = true) }

        if (position < 0) {
            return false
        }

        return position >= config.getIntFromExpression("position", player)
    }
}
