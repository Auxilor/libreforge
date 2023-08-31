package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.runExempted
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.impl.EffectMineVein.breakBlocksSafely
import com.willfp.libreforge.effects.templates.MineBlockEffect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectBreakBlock : MineBlockEffect<NoCompileData>("break_block") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val block = data.block ?: data.location?.block ?: return false

        val player = data.player ?: return false

        player.breakBlocksSafely(listOf(block))

        return true
    }
}
