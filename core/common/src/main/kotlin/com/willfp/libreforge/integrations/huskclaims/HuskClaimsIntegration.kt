package com.willfp.libreforge.integrations.huskclaims

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskclaims.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskclaims.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.huskclaims.impl.TriggerEnterClaimedLand
import com.willfp.libreforge.integrations.huskclaims.impl.TriggerLeaveClaimedLand
import com.willfp.libreforge.integrations.huskclaims.impl.TriggerUnclaimLand
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