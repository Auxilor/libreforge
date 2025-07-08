package com.willfp.libreforge.integrations.huskintegration.huskclaims

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerEnterClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerLeaveClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerUnclaim
import com.willfp.libreforge.triggers.Triggers

object HuskClaimsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionInOwnClaim)
        Triggers.register(TriggerClaim)
        Triggers.register(TriggerEnterClaim)
        Triggers.register(TriggerLeaveClaim)
        Triggers.register(TriggerUnclaim)
    }

    override fun getPluginName(): String {
        return "HuskClaims"
    }
}