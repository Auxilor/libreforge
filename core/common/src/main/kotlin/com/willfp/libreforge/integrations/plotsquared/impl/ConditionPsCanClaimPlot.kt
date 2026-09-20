package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.plotsquared.plotPlayer
import org.bukkit.entity.Player

object ConditionPsCanClaimPlot : Condition<NoCompileData>("ps_can_claim_plot") {
    override val description = "Passes when the player has not reached their PlotSquared plot limit."
    override val categories = setOf("player")
    override val additionalInfo = listOf("Requires the PlotSquared plugin.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val plotPlayer = dispatcher.get<Player>()?.plotPlayer ?: return false

        return plotPlayer.plotCount < plotPlayer.allowedPlots
    }
}
