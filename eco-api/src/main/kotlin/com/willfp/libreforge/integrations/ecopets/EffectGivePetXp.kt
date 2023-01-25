package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGivePetXp : Effect(
    "give_pet_xp",
    triggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("pet", "You must specify the pet to give xp for!")
    }

    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        EcoPetsAPI.instance.givePetExperience(
            player,
            Pets.getByID(config.getString("pet")) ?: return,
            config.getDoubleFromExpression("amount", player)
        )
    }
}
