package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.LightningUtils
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSmite : Effect<NoCompileData>("smite") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("damage", "You must specify the damage to deal!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val damage = config.getDoubleFromExpression("damage", data.player)

        LightningUtils.strike(victim, damage)

        return true
    }
}
