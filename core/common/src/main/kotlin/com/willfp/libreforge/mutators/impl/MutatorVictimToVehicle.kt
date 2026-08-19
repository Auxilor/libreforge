package com.willfp.libreforge.mutators.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.mutators.Mutator
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.LivingEntity

object MutatorVictimToVehicle : Mutator<NoCompileData>("victim_to_vehicle") {
    override val description = "Sets the victim to the entity that the victim is riding."

    override val categories = setOf("victim", "entity")

    override val additionalInfo = listOf("No-ops if the victim is not riding a living entity.")

    override fun mutate(data: TriggerData, config: Config, compileData: NoCompileData): TriggerData {
        return data.copy(
            victim = data.victim?.vehicle as? LivingEntity ?: return data
        )
    }
}
