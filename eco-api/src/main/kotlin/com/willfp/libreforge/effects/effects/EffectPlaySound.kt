package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Sound

class EffectPlaySound : Effect(
    "play_sound",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val sound = Sound.valueOf(config.getString("sound").uppercase())
        val pitch = config.getDoubleFromExpression("pitch")
        val volume = config.getDoubleFromExpression("volume")

        player.playSound(player.location, sound, volume.toFloat(), pitch.toFloat())
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("sound")) violations.add(
            ConfigViolation(
                "sound",
                "You must specify the sound to play!"
            )
        )

        if (!config.has("pitch")) violations.add(
            ConfigViolation(
                "pitch",
                "You must specify the pitch of the sound (0.5-2)!"
            )
        )

        if (!config.has("volume")) violations.add(
            ConfigViolation(
                "volume",
                "You must specify the volume of the sound!"
            )
        )

        return violations
    }
}