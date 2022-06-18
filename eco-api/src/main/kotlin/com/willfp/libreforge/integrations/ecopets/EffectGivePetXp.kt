package com.willfp.libreforge.integrations.ecopets

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers

class EffectGivePetXp : Effect(
    "give_pet_xp",
    applicableTriggers = Triggers.withParameters(
        TriggerParameter.PLAYER
    )
) {
    override fun handle(data: TriggerData, config: Config) {
        val player = data.player ?: return

        EcoPetsAPI.instance.givePetExperience(
            player,
            Pets.getByID(config.getString("pet")) ?: return,
            config.getDoubleFromExpression("amount", player)
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("amount")) violations.add(
            ConfigViolation(
                "amount",
                "You must specify the amount of xp to give!"
            )
        )

        if (!config.has("pet")) violations.add(
            ConfigViolation(
                "pet",
                "You must specify the pet to give xp for!"
            )
        )

        return violations
    }
}
