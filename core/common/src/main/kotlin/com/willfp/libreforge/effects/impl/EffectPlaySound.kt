package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.sound.PlayableSound
import com.willfp.eco.util.SoundUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.SoundCategory

object EffectPlaySound : Effect<NoCompileData>("play_sound") {
    override val description = "Plays a sound to the triggering player at their location."
    override val categories = setOf("sound")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "sound",
            "You must specify the sound to play!",
            description = "The sound to play. Supports Minecraft sound names and resource pack sounds.",
            type = ArgType.SOUND
        )
        require(
            "pitch",
            "You must specify the sound pitch (0.5-2)!",
            description = "The pitch of the sound, between 0.5 and 2.0. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.2"
        )
        require(
            "volume",
            "You must specify the sound volume!",
            description = "The volume of the sound. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "1.0"
        )
        optional(
            "category",
            description = "The sound category to play the sound in.",
            type = ArgType.STRING,
            default = "MASTER",
            choices = SoundCategory.entries.map { it.name }
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val sound = PlayableSound.create(config) ?: return false
        sound.playTo(player)

        return true
    }
}
