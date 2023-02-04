package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.util.asAudience
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import net.kyori.adventure.title.TitlePart

class EffectSendTitle : Effect(
    "send_title",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("title", "You must specify the title!")
        require("subtitle", "You must specify the subtitle!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val title = config.getString("title")
            .replace("%player%", player.name)
            .let { PlaceholderManager.translatePlaceholders(it, player, config) }
            .formatEco(player)

        val subtitle = config.getString("subtitle")
            .replace("%player%", player.name)
            .let { PlaceholderManager.translatePlaceholders(it, player, config) }
            .formatEco(player)

        val audience = player.asAudience()

        audience.sendTitlePart(TitlePart.TITLE, title.toComponent())
        audience.sendTitlePart(TitlePart.SUBTITLE, subtitle.toComponent())
    }
}
