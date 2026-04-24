package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity

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

        victim.world.strikeLightningEffect(victim.location)
        victim.tryAsLivingEntity()?.damage(damage)

        return true
    }
}
