package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.smartpets.activePet
import com.willfp.libreforge.integrations.smartpets.smartPets
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSpEvolvePet : Effect<NoCompileData>("sp_evolve_pet") {
    override val description = "Evolves the player's active SmartPets pet, the same as using /pet evolve."
    override val categories = setOf("pets")
    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "The pet's level, cost and item requirements still apply."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val pet = player.activePet ?: return false

        return smartPets?.evolvePet(player, pet) ?: false
    }
}
