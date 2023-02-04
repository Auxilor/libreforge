package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.Sound

class EffectPlaySound : Effect(
    "play_sound",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("sound", "You must specify the sound to play!")
        require("pitch", "You must specify the sound pitch (0.5-2)!")
        require("volume", "You must specify the sound volume!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val sound = Sound.valueOf(config.getString("sound").uppercase())
        val pitch = config.getDoubleFromExpression("pitch", data)
        val volume = config.getDoubleFromExpression("volume", data)

        player.playSound(player.location, sound, volume.toFloat(), pitch.toFloat())
    }
}
