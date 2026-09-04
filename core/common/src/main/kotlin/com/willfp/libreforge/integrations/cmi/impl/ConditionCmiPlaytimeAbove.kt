package com.willfp.libreforge.integrations.cmi.impl

import com.Zrips.CMI.CMI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionCmiPlaytimeAbove : Condition<NoCompileData>("cmi_playtime_above") {
    override val description = "Passes when the player has played for more than a specified number of hours."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the CMI plugin.",
        "CMI tracks playtime in milliseconds; it is converted to hours before comparing."
    )

    override val arguments = arguments {
        require(
            "hours",
            "You must specify the number of hours!",
            description = "The number of hours of playtime the player must exceed. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val user = CMI.getInstance()?.playerManager?.getUser(player) ?: return false

        return user.totalPlayTime / 3_600_000.0 > config.getDoubleFromExpression("hours", player)
    }
}
