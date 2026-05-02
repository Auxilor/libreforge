package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.util.Vector

object EffectStun : Effect<NoCompileData>("stun") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("ticks", "You must specify the stun duration in ticks!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false
        val ticks = config.getIntFromExpression("ticks", data)

        var current = 0
        plugin.runnableFactory.create {
            current++
            victim.velocity = Vector(0, 0, 0)
            if (current >= ticks) it.cancel()
        }.runTaskTimer(0L, 1L)

        return true
    }
}
