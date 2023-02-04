package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
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
    override val arguments = arguments {
        require("effect", "You must specify the potion effect!")
        require("level", "You must specify the effect level!")
        require("duration", "You must specify the duration of the effect applied!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val location = data.location ?: return
        val world = location.world ?: return

        val effect = PotionEffectType.getByName(config.getString("effect").uppercase()) ?: return
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
    }
}
