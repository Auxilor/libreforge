package com.willfp.libreforge.triggers.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.DataMutator
import com.willfp.libreforge.triggers.TriggerData

object MutatorLocationToProjectile : DataMutator(
    "location_to_projectile"
) {
    override fun mutate(data: TriggerData, config: Config): TriggerData {
        val projectile = data.projectile
        return data.copy(
            location = projectile?.location
        )
    }
}
