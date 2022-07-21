package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.AreaEffectCloud
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectSpawnPotionCloud : Effect(
    "spawn_potion_cloud",
    triggers = Triggers.withParameters(
        TriggerParameter.LOCATION
    )
), Listener {
    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val effect = PotionEffectType.getByName(config.getString("effect").uppercase()) ?: return
        val level = config.getIntFromExpression("level", data.player)
        val duration = config.getIntFromExpression("duration", data.player)

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
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("effect")) violations.add(
            ConfigViolation(
                "effect",
                "You must specify the potion effect!"
            )
        )

        if (!config.has("level")) violations.add(
            ConfigViolation(
                "level",
                "You must specify the level of the effect!"
            )
        )

        if (!config.has("duration")) violations.add(
            ConfigViolation(
                "duration",
                "You must specify the duration of the potion effect applied!"
            )
        )

        return violations
    }
}
