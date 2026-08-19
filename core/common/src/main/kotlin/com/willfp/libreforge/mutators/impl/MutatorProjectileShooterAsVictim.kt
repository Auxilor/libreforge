package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.mutators.parameterTransformers
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.LivingEntity

object MutatorProjectileShooterAsVictim : Mutator<NoCompileData>("projectile_shooter_as_victim") {
    override val description = "Sets the victim to whoever shot the projectile."

    override val categories = setOf("victim", "entity")

    override val additionalInfo = listOf("Sets victim to null if the projectile was not shot by a living entity.")

    override val parameterTransformers = parameterTransformers {
        TriggerParameter.PROJECTILE becomes TriggerParameter.VICTIM
    }

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            victim = data.projectile?.shooter as? LivingEntity
        )
    }
}
