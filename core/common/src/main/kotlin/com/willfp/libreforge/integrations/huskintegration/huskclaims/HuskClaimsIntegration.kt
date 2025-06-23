package com.willfp.libreforge.integrations.huskintegration.huskclaims

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerEnterClaimedLand
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerLeaveClaimedLand
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerUnclaimLand
import com.willfp.libreforge.triggers.Triggers

object HuskClaimsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerClaimLand)
        Triggers.register(TriggerEnterClaimedLand)
        Triggers.register(TriggerLeaveClaimedLand)
        Triggers.register(TriggerUnclaimLand)
        Conditions.register(ConditionInOwnClaim)
    }

    override fun getPluginName(): String {
        return "HuskClaims"
    }
}