package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object MutatorLocationToProjectile : Mutator<NoCompileData>("location_to_projectile") {
    override val description = "Sets the location to the projectile's current position."

    override val categories = setOf("location", "entity")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PROJECTILE becomes TriggerParameter.LOCATION
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            location = data.projectile?.location
        )
    }
}
