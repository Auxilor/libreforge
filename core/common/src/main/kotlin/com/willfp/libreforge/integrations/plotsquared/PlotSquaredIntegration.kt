package com.willfp.libreforge.integrations.plotsquared

import com.plotsquared.core.PlotAPI
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsCanClaimPlot
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsInOwnPlot
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsInPlot
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsInPlotArea
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsInTrustedPlot
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsOnRoad
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsPlotCountAbove
import com.willfp.libreforge.integrations.plotsquared.impl.ConditionPsPlotCountBelow
import com.willfp.libreforge.integrations.plotsquared.impl.FilterPsAutoClaim
import com.willfp.libreforge.integrations.plotsquared.impl.FilterPsPlotArea
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsAddedToPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsBuyPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsClaimPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsClearPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsEnterPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsLeavePlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsMergePlots
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsTrustedOnPlot
import com.willfp.libreforge.integrations.plotsquared.impl.TriggerPsUnlinkPlots
import com.willfp.libreforge.triggers.Triggers

object PlotSquaredIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerPsClaimPlot)
        Triggers.register(TriggerPsMergePlots)
        Triggers.register(TriggerPsUnlinkPlots)
        Triggers.register(TriggerPsEnterPlot)
        Triggers.register(TriggerPsLeavePlot)
        Triggers.register(TriggerPsClearPlot)
        Triggers.register(TriggerPsBuyPlot)
        Triggers.register(TriggerPsTrustedOnPlot)
        Triggers.register(TriggerPsAddedToPlot)

        Conditions.register(ConditionPsInPlot)
        Conditions.register(ConditionPsInOwnPlot)
        Conditions.register(ConditionPsInTrustedPlot)
        Conditions.register(ConditionPsInPlotArea)
        Conditions.register(ConditionPsOnRoad)
        Conditions.register(ConditionPsPlotCountAbove)
        Conditions.register(ConditionPsPlotCountBelow)
        Conditions.register(ConditionPsCanClaimPlot)

        Filters.register(FilterPsAutoClaim)
        Filters.register(FilterPsPlotArea)

        PlotAPI().registerListener(PlotSquaredEventListener)
    }

    override fun getPluginName(): String {
        return "PlotSquared"
    }
}
