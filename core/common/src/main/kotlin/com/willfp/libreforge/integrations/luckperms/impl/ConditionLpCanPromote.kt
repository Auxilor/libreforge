package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.integrations.luckperms.getTrack
import com.willfp.libreforge.integrations.luckperms.lpUser
import org.bukkit.entity.Player

object ConditionLpCanPromote : Condition<NoCompileData>("lp_can_promote") {
    override val description = "Passes when there is a group above the primary group of the player in a LuckPerms track."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
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
        val group = LuckPermsManager.luckPerms?.groupManager?.getGroup(user.primaryGroup) ?: return false

        return track.getNext(group) != null
    }
}
