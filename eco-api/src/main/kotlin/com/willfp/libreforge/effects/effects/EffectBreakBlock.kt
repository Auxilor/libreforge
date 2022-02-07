package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.BlockUtils
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.runExempted
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectBreakBlock : Effect(
    "break_block",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER,
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val block = data.block ?: data.location?.block ?: return

        val player = data.player ?: return

        player.runExempted {
            BlockUtils.breakBlock(it, block)
        }
    }
}