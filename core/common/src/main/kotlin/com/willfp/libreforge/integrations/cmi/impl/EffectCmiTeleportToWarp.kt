package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCmiTeleportToWarp : Effect<NoCompileData>("cmi_teleport_to_warp") {
    override val description = "Teleports the player to a CMI warp."
    override val categories = setOf("movement")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "Does nothing if no warp with the given name exists."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "warp",
            "You must specify the warp name!",
            description = "The name of the CMI warp to teleport to.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = CMI.getInstance()?.warpManager
            ?.getWarp(config.getString("warp"))
            ?.loc
            ?.bukkitLoc
            ?: return false

        player.teleport(location)

        return true
    }
}
