package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.smartpets.activePet
import org.bukkit.entity.Player

object ConditionSpEvolutionStageAbove : Condition<NoCompileData>("sp_evolution_stage_above") {
    override val description = "Passes when the evolution stage of the player's active SmartPets pet is at least the specified stage."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val arguments = arguments {
        require(
            "stage",
            "You must specify the stage!",
            description = "The evolution stage to compare to.",
            type = ArgType.EXPRESSION,
            example = "10"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val pet = player.activePet ?: return false

        return pet.evolutionStage >= config.getDoubleFromExpression("stage", player)
    }
}
