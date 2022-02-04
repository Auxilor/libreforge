package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.asAudience
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import net.kyori.adventure.title.TitlePart

class EffectSendTitle : Effect(
    "send_title",
    supportsFilters = true,
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        val title = config.getFormattedString("title")
            .replace("%player%", player.name)

        val subtitle = config.getFormattedString("subtitle")
            .replace("%player%", player.name)

        val audience = player.asAudience()

        audience.sendTitlePart(TitlePart.TITLE, title.toComponent())
        audience.sendTitlePart(TitlePart.SUBTITLE, subtitle.toComponent())
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("title")) violations.add(
            ConfigViolation(
                "title",
                "You must specify the title to send!"
            )
        )

        if (!config.has("subtitle")) violations.add(
            ConfigViolation(
                "subtitle",
                "You must specify the subtitle to send!"
            )
        )

        return violations
    }
}