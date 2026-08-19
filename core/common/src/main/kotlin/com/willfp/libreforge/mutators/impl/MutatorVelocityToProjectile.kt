package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorVelocityToProjectile : Mutator<NoCompileData>("velocity_to_projectile") {
    override val description = "Sets the velocity to the projectile's current velocity."

    override val categories = setOf("movement", "entity")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PROJECTILE becomes TriggerParameter.VELOCITY
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            velocity = data.projectile?.velocity
        )
    }
}
