package com.willfp.libreforge.effects.impl.bossbar

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.placeholder.context.PlaceholderContext
import com.willfp.eco.core.placeholder.templates.DynamicPlaceholder
import com.willfp.eco.util.toNiceString
import java.util.regex.Pattern

class BossBarProgressPlaceholder(
    plugin: EcoPlugin,
) : DynamicPlaceholder(plugin, Pattern.compile("boss_bar_[a-z0-9-_]+_progress")) {
    override fun getValue(p0: String, p1: PlaceholderContext): String? {
        val id = p0.removePrefix("boss_bar_").removeSuffix("_progress")
        val bossBars = BossBars.getByID(id)

        return if (bossBars.isEmpty()) {
            null
        } else {
            (bossBars[0].bossBar.progress() * 100).toNiceString()
        }
    }
}
