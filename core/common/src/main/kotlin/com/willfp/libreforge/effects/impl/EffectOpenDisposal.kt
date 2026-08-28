package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit

object EffectOpenDisposal : Effect<NoCompileData>("open_disposal") {
    override val description = "Opens a disposal GUI for the player, discarding anything left inside when it is closed."
    override val categories = setOf("player", "inventory")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        optional(
            "rows",
            description = "The number of rows in the disposal GUI (1-6). Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "3",
            example = "6"
        )
        optional(
            "title",
            description = "The title shown at the top of the disposal GUI.",
            type = ArgType.STRING,
            default = "Disposal",
            example = "&cDisposal"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val rows = (if (config.has("rows")) config.getIntFromExpression("rows", data) else 3).coerceIn(1, 6)
        val title = if (config.has("title")) config.getFormattedString("title", data) else "Disposal"

        player.openInventory(Bukkit.createInventory(null, rows * 9, title.toComponent()))

        return true
    }
}
