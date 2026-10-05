package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.notbounties.totalBounty
import org.bukkit.entity.Player

object ConditionNbBountyAbove : Condition<NoCompileData>("nb_bounty_above") {
    override val description = "Passes when the total NotBounties bounty on the player is more than the specified amount."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the NotBounties plugin.",
        "A player with no bounty has a total bounty of 0."
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount!",
            description = "The amount the total bounty must be more than.",
            type = ArgType.EXPRESSION,
            example = "1000"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.totalBounty > config.getDoubleFromExpression("amount", player)
    }
}
