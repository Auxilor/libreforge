package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.SoundUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Sound
import org.bukkit.Bukkit

object EffectPlaySound : Effect<NoCompileData>("play_sound") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("sound", "You must specify the sound to play!")
        require("pitch", "You must specify the sound pitch (0.5-2)!")
        require("volume", "You must specify the sound volume!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val soundName = config.getFormattedString("sound", data)
        val sound: Sound = try {
            Sound.valueOf(soundName)
        } catch (e: IllegalArgumentException) {
            Bukkit.getLogger().warning("Sound $soundName not found")
            return false
        }

        val pitch = config.getDoubleFromExpression("pitch", data)
        val volume = config.getDoubleFromExpression("volume", data)

        player.playSound(player.location, sound, volume.toFloat(), pitch.toFloat())

        return true
    }
}
