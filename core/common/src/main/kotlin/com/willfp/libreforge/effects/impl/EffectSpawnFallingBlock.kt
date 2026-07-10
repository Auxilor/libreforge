package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Material

object EffectSpawnFallingBlock : Effect<NoCompileData>("spawn_falling_block") {
    override val description = "Spawns a falling block entity above the trigger location."
    override val categories = setOf("world", "visual")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "block",
            "You must specify the block material!",
            description = "The block material to spawn as a falling block.",
            type = ArgType.BLOCK
        )
        optional(
            "height",
            description = "The number of blocks above the trigger location to spawn the falling block.",
            type = ArgType.EXPRESSION,
            default = "0"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val material = Material.matchMaterial(config.getString("block").uppercase()) ?: return false

        if (!material.isBlock) return false

        val height = config.getIntFromExpression("height", data).coerceAtLeast(0)
        val spawnLocation = location.clone().add(0.0, height.toDouble(), 0.0)

        @Suppress("DEPRECATION")
        spawnLocation.world.spawnFallingBlock(spawnLocation, material, 0.toByte())

        return true
    }
}
