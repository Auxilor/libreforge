package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTeleport : Effect<NoCompileData>("teleport") {
    override val description = "Teleports the player to the trigger location, preserving their look direction."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.LOCATION
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = data.location ?: return false
        location.pitch = player.location.pitch
        location.yaw = player.location.yaw
        player.teleport(location)

        return true
    }
}
