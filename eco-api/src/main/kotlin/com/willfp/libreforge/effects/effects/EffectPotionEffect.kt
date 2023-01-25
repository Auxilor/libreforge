package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectPotionEffect : Effect(
    "potion_effect",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("effect", "You must specify a valid potion effect! See here: " +
                "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html", Config::getString) {
            PotionEffectType.getByName(it.uppercase()) != null
        }
        require("level", "You must specify the effect level!")
        require("duration", "You must specify the duration!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val toApply = if (config.getBoolOrNull("apply_to_player") == true) {
            data.player ?: return
        } else {
            data.victim ?: return
        }

        toApply.addPotionEffect(
            PotionEffect(
                PotionEffectType.getByName(config.getString("effect").uppercase())
                    ?: PotionEffectType.INCREASE_DAMAGE,
                config.getIntFromExpression("duration", data),
                config.getIntFromExpression("level", data) - 1,
                plugin.configYml.getBool("potions.ambient.triggered"),
                plugin.configYml.getBool("potions.particles.triggered"),
                plugin.configYml.getBool("potions.icon.triggered")
            )
        )
    }
}
