package com.willfp.libreforge.integrations.huskintegration.husktowns

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionHasTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.FilterTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerChangeTownRole
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerCreateTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerDisbandTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerEnterClaimedLand
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerJoinTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerLeaveClaimedLand
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerLeaveTown
import com.willfp.libreforge.integrations.huskintegration.husktowns.impl.TriggerUnclaimLand
import com.willfp.libreforge.triggers.Triggers

object HuskTownsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerChangeTownRole)
        Triggers.register(TriggerClaimLand)
        Triggers.register(TriggerCreateTown)
        Triggers.register(TriggerDisbandTown)
        Triggers.register(TriggerEnterClaimedLand)
        Triggers.register(TriggerJoinTown)
        Triggers.register(TriggerLeaveTown)
        Triggers.register(TriggerLeaveClaimedLand)
        Triggers.register(TriggerUnclaimLand)
        Filters.register(FilterTownRole)
        Conditions.register(ConditionHasTownRole)
        Conditions.register(ConditionInOwnClaim)
    }

    override fun getPluginName(): String {
        return "HuskTowns"
    }
}