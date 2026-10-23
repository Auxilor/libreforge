package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.bossbar.BossBars
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object EffectRemoveBossBar : Effect<NoCompileData>("remove_boss_bar") {
    override val description = "Removes a boss bar by its ID for the player, or for everyone when there is no player."
    override val categories = setOf("visual")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "id",
            "You must specify the id of the boss bar!",
            description = "The unique ID of the boss bar to remove. Supports placeholders.",
            type = ArgType.STRING,
            example = "boss_%player_name%"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val id = config.getString("id").translatePlaceholders(config.toPlaceholderContext(data))
        val player = data.player

        if (player == null) {
            BossBars.remove(id)
        } else {
            BossBars.remove(id, player.uniqueId)
        }

        return true
    }
}
