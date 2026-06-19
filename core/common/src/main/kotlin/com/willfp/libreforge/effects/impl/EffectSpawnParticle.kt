package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.particle.Particles
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getOrElse
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSpawnParticle : Effect<NoCompileData>(
    "spawn_particle"
) {
    override val description = "Spawns particles at the trigger location."
    override val categories = setOf("visual")

    override val parameters = setOf(
        TriggerParameter.LOCATION
    )

    override val arguments = arguments {
        require(
            "particle",
            "You must specify the particle!",
            description = "The particle to spawn, as an eco particle string.",
            type = ArgType.ANY
        )
        optional(
            "amount",
            description = "The number of particles to spawn. Supports expressions.",
            type = ArgType.EXPRESSION,
            default = "1"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val location = data.location ?: return false
        val particle = Particles.lookup(config.getString("particle"))
        val amount = config.getOrElse("amount", 1) { getIntFromExpression(it, data) }

        plugin.scheduler.runAsync {
            particle.spawn(location, amount)
        }

        return true
    }
}
