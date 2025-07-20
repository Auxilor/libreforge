package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EffectPotionEffect : Effect<NoCompileData>("potion_effect") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "effect",
            "You must specify a valid potion effect! See here: " +
                    "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
            Config::getString
        ) {
            @Suppress("DEPRECATION")
            PotionEffectType.getByName(it.uppercase()) != null
        }
        require("level", "You must specify the effect level!")
        require("duration", "You must specify the duration!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val toApply = if (config.getBoolOrNull("apply_to_player") == true) {
            data.player ?: return false
        } else {
            data.victim ?: return false
        }

        toApply.addPotionEffect(
            PotionEffect(
                @Suppress("DEPRECATION")
                PotionEffectType.getByName(config.getString("effect").uppercase())
                    ?: PotionEffectType.LUCK,
                config.getIntFromExpression("duration", data),
                config.getIntFromExpression("level", data) - 1,
                true,
                config.getBoolOrNull("particles") ?: true,
                config.getBoolOrNull("icon") ?: true
            )
        )

        return true
    }
}
