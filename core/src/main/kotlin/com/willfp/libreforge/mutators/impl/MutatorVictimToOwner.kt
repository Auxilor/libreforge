package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Tameable

object MutatorVictimToOwner : Mutator<NoCompileData>("victim_to_owner") {
    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        val victim = data.victim as? Tameable
        val owner = victim?.owner as? LivingEntity

        return data.copy(
            victim = owner
        )
    }
}
