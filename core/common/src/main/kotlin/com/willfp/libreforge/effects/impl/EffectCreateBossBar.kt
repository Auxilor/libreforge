package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.bossbar.BossBars
import com.willfp.libreforge.effects.impl.bossbar.RegistrableBossBar
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.kyori.adventure.bossbar.BossBar

object EffectCreateBossBar : Effect<NoCompileData>("create_boss_bar") {
    override val description = "Creates and displays a boss bar for the player."
    override val categories = setOf("visual")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "id",
            "You must specify the id of the boss bar!",
            description = "A unique identifier used to reference this boss bar with update_boss_bar or remove_boss_bar.",
            type = ArgType.STRING
        )
        require(
            "name",
            "You must specify the name of the boss bar!",
            description = "The display text shown on the boss bar. Supports placeholders.",
            type = ArgType.STRING
        )
        require("color", "You must specify a valid boss bar color!", Config::getFormattedString) {
            enumValueOfOrNull<BossBar.Color>(it.uppercase()) != null
        }
        describe(
            "color",
            description = "The boss bar color.",
            type = ArgType.STRING,
            choices = listOf("BLUE", "GREEN", "PINK", "PURPLE", "RED", "WHITE", "YELLOW")
        )
        require("style", "You must specify a valid boss bar style!", Config::getFormattedString) {
            enumValueOfOrNull<BossBar.Overlay>(it.uppercase()) != null
        }
        describe(
            "style",
            description = "The boss bar overlay style.",
            type = ArgType.STRING,
            choices = listOf("PROGRESS", "NOTCHED_6", "NOTCHED_10", "NOTCHED_12", "NOTCHED_20")
        )
        require(
            "progress",
            "You must specify the boss bar progress percentage!",
            description = "The fill percentage of the boss bar, from 0 to 100. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val name = config.getFormattedString("name", data)
        val color = enumValueOfOrNull<BossBar.Color>(config.getFormattedString("color", data).uppercase()) ?: return false
        val style = enumValueOfOrNull<BossBar.Overlay>(config.getFormattedString("style", data).uppercase()) ?: return false
        val progress = config.getDoubleFromExpression("progress", data).coerceIn(0.0, 100.0).toFloat() / 100f

        val bossBar = BossBar.bossBar(
            name.toComponent(),
            progress,
            color,
            style
        )

        BossBars.register(
            RegistrableBossBar(
                config.getString("id").translatePlaceholders(config.toPlaceholderContext(data)),
                bossBar,
                player.uniqueId
            )
        )

        return true
    }
}
