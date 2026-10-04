package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.model.PetStatType
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

object ConditionSpStatAbove : Condition<NoCompileData>("sp_stat_above") {
    override val description = "Passes when a stat of the player's active SmartPets pet is at least the specified amount."
    override val categories = setOf("pets")
    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Stats are TRUST, LOYALTY, HUNGER, ENERGY, AFFECTION, HAPPINESS, FEAR and ANGER, from 0 to 100."
    )

    override val arguments = arguments {
        require(
            "stat",
            "You must specify the stat!",
            description = "The stat to check.",
            type = ArgType.STRING,
            example = "loyalty"
        )
        require(
            "amount",
            "You must specify the amount!",
            description = "The amount to compare the stat to.",
            type = ArgType.EXPRESSION,
            example = "50"
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
        val stat = PetStatType.fromString(config.getString("stat")) ?: return false

        return pet.stats.get(stat) >= config.getDoubleFromExpression("amount", player)
    }
}
