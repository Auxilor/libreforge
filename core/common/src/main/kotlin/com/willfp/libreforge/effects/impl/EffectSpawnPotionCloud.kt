package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EffectSpawnPotionCloud : Effect<NoCompileData>("spawn_potion_cloud") {
    override val description = "Spawns a lingering potion cloud at the trigger location that applies a potion effect."
    override val categories = setOf("potion", "visual")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "effect",
            "You must specify the potion effect!",
            description = "The potion effect to apply to entities in the cloud.",
            type = ArgType.POTION_EFFECT
        )
        require(
            "level",
            "You must specify the effect level!",
            description = "The amplifier level of the potion effect. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% / 10"
        )
        require(
            "duration",
            "You must specify the duration of the effect applied!",
            description = "How long the cloud and its applied effect last, in ticks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val world = location.world ?: return false

        @Suppress("DEPRECATION")
        val effect = PotionEffectType.getByName(config.getString("effect").uppercase()) ?: return false
        val level = config.getIntFromExpression("level", data)
        val duration = config.getIntFromExpression("duration", data)

        val cloud = world.spawn(location, AreaEffectCloud::class.java)
        cloud.addCustomEffect(
            PotionEffect(
                effect,
                duration,
                level - 1
            ),
            true
        )

        cloud.source = data.player
        cloud.duration = duration

        return true
    }
}
