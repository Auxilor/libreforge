package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.model.PetStatType
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

object EffectSpGiveStat : Effect<NoCompileData>("sp_give_stat") {
    override val description = "Adds to a stat of the player's active SmartPets pet, or takes away from it when the amount is negative."
    override val categories = setOf("pets")
    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Stats are TRUST, LOYALTY, HUNGER, ENERGY, AFFECTION, HAPPINESS, FEAR and ANGER, kept between 0 and 100."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to change.",
            type = ArgType.STRING,
            example = "loyalty"
        )
        require(
            "amount",
            "You must specify the amount!",
            description = "The amount to add. Negative values take away.",
            type = ArgType.EXPRESSION,
            example = "5"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val pet = player.activePet ?: return false
        val stat = PetStatType.fromString(config.getString("stat")) ?: return false

        return smartPets?.modifyPetStat(
            player.uniqueId,
            pet,
            stat,
            config.getDoubleFromExpression("amount", data).toInt()
        ) ?: false
    }
}
