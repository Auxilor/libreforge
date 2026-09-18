package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.plotsquared.plotPlayer
import org.bukkit.entity.Player

object ConditionPsPlotCountAbove : Condition<NoCompileData>("ps_plot_count_above") {
    override val description = "Passes when the player owns more than the specified number of PlotSquared plots."
    override val categories = setOf("player")
    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Counts plots the same way PlotSquared does for the plot limit."
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount!",
            description = "The number of plots the player must have more than.",
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
        val plotPlayer = player.plotPlayer ?: return false

        return plotPlayer.plotCount > config.getIntFromExpression("amount", player)
    }
}
