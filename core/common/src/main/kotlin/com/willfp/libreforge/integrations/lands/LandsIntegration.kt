package com.willfp.libreforge.integrations.lands

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.lands.impl.ConditionHasClaimRole
import com.willfp.libreforge.integrations.lands.impl.ConditionInEnemyClaim
import com.willfp.libreforge.integrations.lands.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.lands.impl.ConditionInTrustedClaim
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceAbove
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceBelow
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceEqual
import com.willfp.libreforge.integrations.lands.impl.EffectGiveLandsBankBalance
import com.willfp.libreforge.integrations.lands.impl.FilterAtWarWithVictim
import com.willfp.libreforge.integrations.lands.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.lands.impl.TriggerEnterClaimedLand
import com.willfp.libreforge.integrations.lands.impl.TriggerJoinLand
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankDeposit
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankWithdraw
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsSpawnTeleport
import com.willfp.libreforge.integrations.lands.impl.TriggerLeaveClaimedLand
import com.willfp.libreforge.integrations.lands.impl.TriggerLeaveLand
import com.willfp.libreforge.integrations.lands.impl.TriggerUnclaimLand
import com.willfp.libreforge.triggers.Triggers

object LandsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasClaimRole) // Working
        Conditions.register(ConditionInEnemyClaim) // TBC
        Conditions.register(ConditionInTrustedClaim) // Working
        Conditions.register(ConditionInOwnClaim) // Working
        Conditions.register(ConditionLandsBalanceAbove) // Working
        Conditions.register(ConditionLandsBalanceBelow) // Working
        Conditions.register(ConditionLandsBalanceEqual) // Working
        Effects.register(EffectGiveLandsBankBalance) // Working
        Filters.register(FilterAtWarWithVictim) // Working
        Triggers.register(TriggerClaimLand) // Working with single and selection
        Triggers.register(TriggerEnterClaimedLand) // Working
        Triggers.register(TriggerJoinLand) // Working
        Triggers.register(TriggerLandsBankDeposit) // Working
        Triggers.register(TriggerLandsBankWithdraw) // Working
        Triggers.register(TriggerLandsSpawnTeleport) // Working
        Triggers.register(TriggerLeaveClaimedLand) // Working
        Triggers.register(TriggerLeaveLand) // Working
        Triggers.register(TriggerUnclaimLand) // Working with single, all and selection
    }

    override fun getPluginName(): String? {
        return "Lands"
    }
}