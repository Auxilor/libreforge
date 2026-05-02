package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTimeBomb : Effect<NoCompileData>("time_bomb") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("fuse", "You must specify the fuse duration in ticks!")
        require("power", "You must specify the explosion power!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val fuse = config.getIntFromExpression("fuse", data)
        val power = config.getDoubleFromExpression("power", data)
        val breakBlocks = config.getBoolOrNull("break_blocks") ?: false
        val glow = config.getBoolOrNull("glow") ?: true

        if (glow) victim.isGlowing = true

        plugin.scheduler.runLater(fuse.toLong()) {
            if (glow) victim.isGlowing = false
            if (!victim.isDead) {
                val loc = victim.location
                loc.world?.createExplosion(loc, power.toFloat(), false, breakBlocks, victim)
            }
        }

        return true
    }
}
