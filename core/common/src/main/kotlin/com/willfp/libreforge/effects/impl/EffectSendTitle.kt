package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.asAudience
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.kyori.adventure.title.TitlePart

object EffectSendTitle : Effect<NoCompileData>("send_title") {
    override val description = "Sends a title and subtitle to the player's screen."
    override val categories = setOf("visual", "chat")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "title",
            "You must specify the title!",
            description = "The main title text to display. Supports placeholders.",
            type = ArgType.STRING
        )
        require(
            "subtitle",
            "You must specify the subtitle!",
            description = "The subtitle text to display below the title. Supports placeholders.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val title = config.getString("title")
            .replace("%player%", player.name)
            .formatEco(config.toPlaceholderContext(data))

        val subtitle = config.getString("subtitle")
            .replace("%player%", player.name)
            .formatEco(config.toPlaceholderContext(data))

        val audience = player.asAudience()

        audience.sendTitlePart(TitlePart.TITLE, title.toComponent())
        audience.sendTitlePart(TitlePart.SUBTITLE, subtitle.toComponent())

        return true
    }
}
