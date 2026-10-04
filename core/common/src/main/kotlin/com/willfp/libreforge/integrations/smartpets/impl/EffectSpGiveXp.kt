package com.willfp.libreforge.integrations.smartpets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.integrations.smartpets.activePet
import com.willfp.libreforge.integrations.smartpets.smartPets
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSpGiveXp : Effect<NoCompileData>("sp_give_xp") {
    override val description = "Gives experience to the player's active SmartPets pet and handles level ups."
    override val categories = setOf("pets")
    override val additionalInfo = listOf("Requires SmartPets Pro to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of xp to give!",
            description = "The amount of experience to give.",
            type = ArgType.EXPRESSION,
            example = "25"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val pet = player.activePet ?: return false
        val amount = config.getDoubleFromExpression("amount", data).toInt()

        if (amount <= 0) {
            return false
        }

        return smartPets?.addPetExperience(player.uniqueId, pet, amount) ?: false
    }
}
