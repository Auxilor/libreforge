package com.willfp.libreforge.integrations.huskintegration.husktowns

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionHasTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionHasTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionInAnyClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.FilterTownName
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.FilterTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerChangeTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerCreateTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerDisbandTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerEnterClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerExitClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerJoinTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerLeaveTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerUnclaim
import com.willfp.libreforge.triggers.Triggers

object HuskTownsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerChangeTownRole) // Seperate
        Triggers.register(TriggerClaim) // Combined
        Triggers.register(TriggerCreateTown) // Seperate
        Triggers.register(TriggerDisbandTown) // Seperate
        Triggers.register(TriggerEnterClaim) // Combined
        Triggers.register(TriggerJoinTown) // Seperate
        Triggers.register(TriggerLeaveTown) // Seperate
        Triggers.register(TriggerExitClaim) // Combined
        Triggers.register(TriggerUnclaim) // Combined
        Filters.register(FilterTownRole) // Seperate
        Filters.register(FilterTownName) // Seperate
        Conditions.register(ConditionHasTownRole) // Seperate
        Conditions.register(ConditionHasTown) // Seperate
        Conditions.register(ConditionInOwnClaim) // Combined
        Conditions.register(ConditionInAnyClaim) // Combined
    }

    override fun getPluginName(): String {
        return "HuskTowns"
    }
}