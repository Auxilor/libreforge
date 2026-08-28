package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTeleportToLookingAt : Effect<NoCompileData>("teleport_to_looking_at") {
    override val description = "Teleports the player on top of the block they are looking at."
    override val categories = setOf("movement")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "distance",
            description = "The maximum distance to search for a block. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "128",
            example = "%level% * 10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val location = player.location

        val distance = if (config.has("distance")) config.getDoubleFromExpression("distance", data) else 128.0

        val block = player.rayTraceBlocks(distance)?.hitBlock ?: return false

        val target = block.location.add(0.5, 1.0, 0.5)
        target.yaw = location.yaw
        target.pitch = location.pitch

        player.teleport(target)

        return true
    }
}
