package com.willfp.libreforge.integrations.lands

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.lands.impl.ConditionHasLandsRole
import com.willfp.libreforge.integrations.lands.impl.ConditionInOwnClaim
import com.willfp.libreforge.integrations.lands.impl.ConditionInTrustedClaim
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceAbove
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceBelow
import com.willfp.libreforge.integrations.lands.impl.ConditionLandsBalanceEqual
import com.willfp.libreforge.integrations.lands.impl.EffectGiveLandsBalance
import com.willfp.libreforge.integrations.lands.impl.EffectSetLandsBalance
import com.willfp.libreforge.integrations.lands.impl.FilterAtWarWithVictim
import com.willfp.libreforge.integrations.lands.impl.TriggerClaim
import com.willfp.libreforge.integrations.lands.impl.TriggerEnterClaim
import com.willfp.libreforge.integrations.lands.impl.TriggerExitClaim
import com.willfp.libreforge.integrations.lands.impl.TriggerJoinLand
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankDeposit
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankWithdraw
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsSpawnTeleport
import com.willfp.libreforge.integrations.lands.impl.TriggerLeaveLand
import com.willfp.libreforge.integrations.lands.impl.TriggerUnclaim
import com.willfp.libreforge.triggers.Triggers

object LandsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasLandsRole)
        Conditions.register(ConditionInOwnClaim)
        Conditions.register(ConditionInTrustedClaim)
        Conditions.register(ConditionLandsBalanceAbove)
        Conditions.register(ConditionLandsBalanceBelow)
        Conditions.register(ConditionLandsBalanceEqual)
        Effects.register(EffectGiveLandsBalance)
        Effects.register(EffectSetLandsBalance)
        Filters.register(FilterAtWarWithVictim)
        Triggers.register(TriggerClaim)
        Triggers.register(TriggerEnterClaim)
        Triggers.register(TriggerExitClaim)
        Triggers.register(TriggerJoinLand)
        Triggers.register(TriggerLandsBankDeposit)
        Triggers.register(TriggerLandsBankWithdraw)
        Triggers.register(TriggerLandsSpawnTeleport)
        Triggers.register(TriggerLeaveLand)
        Triggers.register(TriggerUnclaim)
    }

    override fun getPluginName(): String? {
        return "Lands"
    }
}