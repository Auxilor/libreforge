package com.willfp.libreforge.integrations.lands

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.lands.impl.ConditionHasClaimRole
import com.willfp.libreforge.integrations.lands.impl.ConditionInTrustedClaim
import com.willfp.libreforge.integrations.lands.impl.FilterAtWarWithVictim
import com.willfp.libreforge.integrations.lands.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.lands.impl.TriggerJoinLand
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankDeposit
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankWithdraw
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsSpawnTeleport
import com.willfp.libreforge.integrations.lands.impl.TriggerLeaveLand
import com.willfp.libreforge.integrations.lands.impl.TriggerUnclaimLand
import com.willfp.libreforge.triggers.Triggers

object LandsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasClaimRole)
        Conditions.register(ConditionInTrustedClaim)
        Filters.register(FilterAtWarWithVictim)
        Triggers.register(TriggerClaimLand)
        Triggers.register(TriggerJoinLand)
        Triggers.register(TriggerLandsBankDeposit)
        Triggers.register(TriggerLandsBankWithdraw)
        Triggers.register(TriggerLandsSpawnTeleport)
        Triggers.register(TriggerLeaveLand)
        Triggers.register(TriggerUnclaimLand)
    }

    override fun getPluginName(): String? {
        return "Lands"
    }
}