package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.eco.util.toComponent
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
    override val isPermanent = false

    override val arguments = arguments {
        require("id", "You must specify the id of the boss bar!")
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
