package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGiveXp : Effect<NoCompileData>("give_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (Prerequisite.HAS_PAPER.isMet) {
            player.giveExp(config.getIntFromExpression("amount", data), config.getBoolOrNull("apply_mending") ?: true)
        } else {
            player.giveExp(config.getIntFromExpression("amount", data))
        }

        return true
    }
}
