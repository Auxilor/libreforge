package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.luckperms.lpQueryOptions
import com.willfp.libreforge.integrations.luckperms.lpUser
import org.bukkit.entity.Player

object ConditionLpGroupCountAbove : Condition<NoCompileData>("lp_group_count_above") {
    override val description = "Passes when the player is in at least the specified number of LuckPerms groups."
    override val categories = setOf("permission")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Counts inherited groups."
    )

    override val arguments = arguments {
        require(
            "count",
            "You must specify the group count!",
            description = "The minimum number of groups the player must be in.",
            type = ArgType.EXPRESSION,
            example = "3"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = player.lpUser ?: return false

        return user.getInheritedGroups(player.lpQueryOptions).size >= config.getIntFromExpression("count", player)
    }
}
