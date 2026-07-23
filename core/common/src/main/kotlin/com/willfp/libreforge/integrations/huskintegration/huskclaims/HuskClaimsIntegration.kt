package com.willfp.libreforge.integrations.huskintegration.huskclaims

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInAdminClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.ConditionInTrustedClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerDeleteClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerEnterClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerLeaveClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerTransferClaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerTrustPlayer
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerUnclaim
import com.willfp.libreforge.integrations.huskintegration.huskclaims.impl.TriggerUntrustPlayer
import com.willfp.libreforge.triggers.Triggers

object HuskClaimsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerClaim)
        Triggers.register(TriggerEnterClaim)
        Triggers.register(TriggerLeaveClaim)
        Triggers.register(TriggerUnclaim)
        Triggers.register(TriggerTrustPlayer)
        Triggers.register(TriggerUntrustPlayer)
        Triggers.register(TriggerTransferClaim)
        Triggers.register(TriggerDeleteClaim)
        Conditions.register(ConditionInOwnClaim)
        Conditions.register(ConditionInTrustedClaim)
        Conditions.register(ConditionInClaim)
        Conditions.register(ConditionInAdminClaim)
    }

    override fun getPluginName(): String {
        return "HuskClaims"
    }
}