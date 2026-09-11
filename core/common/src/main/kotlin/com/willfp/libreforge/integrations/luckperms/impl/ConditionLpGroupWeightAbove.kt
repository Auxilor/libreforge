package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import org.bukkit.entity.Player

object ConditionLpGroupWeightAbove : Condition<NoCompileData>("lp_group_weight_above") {
    override val description = "Passes when the weight of the specified LuckPerms group is at or above the minimum."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Fails if the group is not loaded or has no weight set."
    )

    override val arguments = arguments {
        require(
            "group",
            "You must specify the group!",
            description = "The name of the LuckPerms group.",
            type = ArgType.STRING,
            example = "vip"
        )
        require(
            "weight",
            "You must specify the minimum weight!",
            description = "The minimum weight the group must have.",
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
        val group = LuckPermsManager.luckPerms?.groupManager?.getGroup(config.getString("group")) ?: return false
        val weight = group.weight

        if (weight.isEmpty) {
            return false
        }

        return weight.asInt >= config.getIntFromExpression("weight", player)
    }
}
