package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import me.jadenp.notbounties.data.Whitelist
import me.jadenp.notbounties.utils.BountyManager
import java.util.TreeSet

object EffectNbAddBounty : Effect<NoCompileData>("nb_add_bounty") {
    override val description = "Adds a NotBounties bounty on the player, set by the console."
    override val categories = setOf("player")
    override val additionalInfo = listOf("Requires the NotBounties plugin.")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount!",
            description = "The amount to add to the bounty on the player.",
            type = ArgType.EXPRESSION,
            example = "%level% * 100"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val amount = config.getDoubleFromExpression("amount", data)

        if (amount <= 0) {
            return false
        }

        BountyManager.addBounty(player, amount, emptyList(), Whitelist(TreeSet(), false))

        return true
    }
}
