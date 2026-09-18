package com.willfp.libreforge.integrations.plotsquared.impl

import com.plotsquared.core.events.PlotClaimedNotifyEvent
import com.plotsquared.core.events.PlotEvent
import com.plotsquared.core.events.post.PostPlayerAutoPlotEvent
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.integrations.plotsquared.asPlotSquaredEvent
import com.willfp.libreforge.triggers.TriggerData

object FilterPsAutoClaim : Filter<NoCompileData, Boolean>("ps_auto_claim") {
    override val description = "Matches when the PlotSquared plot was, or was not, claimed with /plot auto."
    override val categories = setOf("world")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf(
        "Requires the PlotSquared plugin.",
        "Fails when the trigger is not a PlotSquared claim."
    )

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        return when (data.event.asPlotSquaredEvent<PlotEvent>()) {
            is PostPlayerAutoPlotEvent -> value
            is PlotClaimedNotifyEvent -> !value
            else -> false
        }
    }
}
