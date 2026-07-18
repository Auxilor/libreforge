package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object EffectPotionEffect : Effect<NoCompileData>("potion_effect") {
    override val description = "Applies a potion effect to the player or victim for a configurable duration and level."
    override val categories = setOf("potion")

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
        describe(
            "effect",
            description = "The potion effect type to apply.",
            type = ArgType.POTION_EFFECT
        )
        require(
            "level",
            "You must specify the effect level!",
            description = "The level of the potion effect (1 = level I). Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1 + %level% / 10"
        )
        require(
            "duration",
            "You must specify the duration!",
            description = "How long the effect lasts in ticks. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "20 * %level%"
        )
        optional(
            "apply_to_player",
            description = "If true, applies the effect to the player instead of the victim.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
        optional(
            "particles",
            description = "Whether to show potion effect particles.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
        optional(
            "icon",
            description = "Whether to show the effect icon in the HUD.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
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