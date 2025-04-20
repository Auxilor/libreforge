package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTransmission : Effect<NoCompileData>("transmission") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("distance", "You must specify the distance to transmit!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val distance = config.getDoubleFromExpression("distance", data)

        val movement = player.eyeLocation.direction.clone()
            .normalize()
            .multiply(distance)

        var location = player.eyeLocation.clone()
            .add(movement)

        val ray = player.rayTraceBlocks(distance)

        if (ray != null) {
            val hitBlock = ray.hitBlock ?: return false
            val hitBlockFace = ray.hitBlockFace ?: return false

            location = hitBlock.getRelative(hitBlockFace).location
                .add(0.5, 0.5 - player.eyeHeight, 0.5)
        }

        if (location.block.isSolid || location.toVector().distance(player.location.toVector()) < 0.5) {
            @Suppress("UsagesOfObsoleteApi")
            player.sendMessage(plugin.langYml.getFormattedString("messages.cannot-transmit"))
            return false
        }

        location.pitch = player.location.pitch
        location.yaw = player.location.yaw

        return player.teleport(location)
    }
}
