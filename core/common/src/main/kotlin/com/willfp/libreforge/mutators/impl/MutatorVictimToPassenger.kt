package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.LivingEntity

object MutatorVictimToPassenger : Mutator<NoCompileData>("victim_to_passenger") {
    override val description = "Sets the victim to the entity that is riding the victim."

    override val categories = setOf("victim", "entity")

    override val additionalInfo = listOf("No-ops if the victim has no living entity riding it.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            victim = data.victim?.passengers?.filterIsInstance<LivingEntity>()?.firstOrNull() ?: return data
        )
    }
}
