package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpMetaData
import org.bukkit.entity.Player

object ConditionLpWeightBelow : Condition<NoCompileData>("lp_weight_below") {
    override val description = "Passes when the LuckPerms weight of the player is at or below the maximum."
    override val categories = setOf("permission")
    override val additionalInfo = listOf("Requires the LuckPerms plugin.")

    override val arguments = arguments {
        require(
            "weight",
            "You must specify the maximum weight!",
            description = "The maximum weight the player can have.",
            type = ArgType.EXPRESSION,
            example = "100"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val weight = player.lpMetaData?.weight ?: return false

        return weight <= config.getIntFromExpression("weight", player)
    }
}
