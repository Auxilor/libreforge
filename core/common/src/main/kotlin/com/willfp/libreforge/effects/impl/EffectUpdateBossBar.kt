package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.eco.util.toComponent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.bossbar.BossBars
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import net.kyori.adventure.bossbar.BossBar

object EffectUpdateBossBar : Effect<NoCompileData>("update_boss_bar") {
    override val description = "Updates the properties of an existing boss bar created by create_boss_bar."
    override val categories = setOf("visual")

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "id",
            "You must specify the id of the boss bar!",
            description = "The ID of the boss bar to update.",
            type = ArgType.STRING,
            example = "boss_%player_name%"
        )
        optional(
            "progress",
            description = "The new progress value (0–100). Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "",
            example = "50"
        )
        optional(
            "name",
            description = "The new display name of the boss bar. Supports placeholders.",
            type = ArgType.STRING,
            default = "",
            example = "%player_name%'s Boss"
        )
        optional(
            "color",
            description = "The new color of the boss bar (e.g. BLUE, RED).",
            type = ArgType.STRING,
            default = "",
            example = "RED"
        )
        optional(
            "style",
            description = "The new overlay style of the boss bar (e.g. PROGRESS, NOTCHED_10).",
            type = ArgType.STRING,
            default = "",
            example = "NOTCHED_10"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val id = config.getString("id").translatePlaceholders(config.toPlaceholderContext(data))

        if (config.has("progress")) {
            BossBars.update(id) {
                it.progress(
                    config.getDoubleFromExpression("progress", data).coerceIn(0.0, 100.0).toFloat() / 100f
                )
            }
        }

        if (config.has("name")) {
            BossBars.update(
                id
            ) {
                it.name(config.getFormattedString("name", data).toComponent())
            }
        }

        if (config.has("color")) {
            val color = enumValueOfOrNull<BossBar.Color>(config.getFormattedString("color", data).uppercase())

            if (color != null) {
                BossBars.update(id) {
                    it.color(color)
                }
            }
        }

        if (config.has("style")) {
            val style = enumValueOfOrNull<BossBar.Overlay>(config.getFormattedString("style", data).uppercase())

            if (style != null) {
                BossBars.update(id) {
                    it.overlay(style)
                }
            }
        }

        return true
    }
}
