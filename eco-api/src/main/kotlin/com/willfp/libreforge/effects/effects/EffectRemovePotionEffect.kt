package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.potion.PotionEffectType

class EffectRemovePotionEffect : Effect(
    "remove_potion_effect",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("effect", "You must specify a valid potion effect! See here: " +
                "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html", Config::getString) {
            PotionEffectType.getByName(it.uppercase()) != null
        }
    }

    override fun handle(data: TriggerData, config: Config) {
        val toApply = if (config.getBoolOrNull("apply_to_player") == true) {
            data.player ?: return
        } else {
            data.victim ?: return
        }

        plugin.scheduler.run {
            toApply.removePotionEffect(
                PotionEffectType.getByName(config.getString("effect").uppercase())
                    ?: PotionEffectType.INCREASE_DAMAGE
            )
        }
    }
}
