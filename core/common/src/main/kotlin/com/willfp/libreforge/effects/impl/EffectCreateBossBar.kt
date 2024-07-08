package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.eco.util.toComponent
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
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("id", "You must specify the id of the boss bar!")
        require("name", "You must specify the name of the boss bar!")
        require("color", "You must specify a valid boss bar color!", Config::getFormattedString) {
            enumValueOfOrNull<BossBar.Color>(it.uppercase()) != null
        }
        require("style", "You must specify a valid boss bar style!", Config::getFormattedString) {
            enumValueOfOrNull<BossBar.Overlay>(it.uppercase()) != null
        }
        require("progress", "You must specify the boss bar progress percentage!")
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
