package com.willfp.libreforge.integrations.plotsquared.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.plotsquared.currentPlot
import org.bukkit.entity.Player

object ConditionPsInPlot : Condition<NoCompileData>("ps_in_plot") {
    override val description = "Passes when the player is standing inside a claimed PlotSquared plot."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the PlotSquared plugin.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.currentPlot?.hasOwner() == true
    }
}
