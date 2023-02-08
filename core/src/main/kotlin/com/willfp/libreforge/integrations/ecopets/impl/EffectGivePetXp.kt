package com.willfp.libreforge.integrations.ecopets.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecopets.api.EcoPetsAPI
import com.willfp.ecopets.pets.Pets
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectGivePetXp : Effect<NoCompileData>("give_pet_xp") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount of xp to give!")
        require("pet", "You must specify the pet to give xp for!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        EcoPetsAPI.instance.givePetExperience(
            player,
            Pets.getByID(config.getString("pet")) ?: return false,
            config.getDoubleFromExpression("amount", player)
        )

        return true
    }
}
