package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.bossbar.BossBars
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData

object EffectRemoveBossBar : Effect<NoCompileData>("remove_boss_bar") {
    override val isPermanent = false

    override val arguments = arguments {
        require("id", "You must specify the id of the boss bar!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        BossBars.remove(config.getString("id").translatePlaceholders(config.toPlaceholderContext(data)))

        return true
    }
}
