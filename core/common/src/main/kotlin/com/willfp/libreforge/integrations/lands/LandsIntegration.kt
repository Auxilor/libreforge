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
        Conditions.register(ConditionHasLandsRole) // Seperate
        Conditions.register(ConditionInTrustedClaim) // Combined (not applicable though
        Conditions.register(ConditionInOwnClaim) // Combined
        Conditions.register(ConditionLandsBalanceAbove) // Seperate
        Conditions.register(ConditionLandsBalanceBelow) // Seperate
        Conditions.register(ConditionLandsBalanceEqual) // Seperate
        Effects.register(EffectGiveLandsBalance) // Seperate
        Effects.register(EffectSetLandsBalance) // Seperate
        Filters.register(FilterAtWarWithVictim) // Potentially combined
        Triggers.register(TriggerClaim) // Combined
        Triggers.register(TriggerEnterClaim) // Combined
        Triggers.register(TriggerJoinLand) // Seperate
        Triggers.register(TriggerLandsBankDeposit) // Seperate
        Triggers.register(TriggerLandsBankWithdraw) // Seperate
        Triggers.register(TriggerLandsSpawnTeleport) // Seperate
        Triggers.register(TriggerExitClaim) // Combined
        Triggers.register(TriggerLeaveLand) // Seperate
        Triggers.register(TriggerUnclaim) // Combined
    }

    override fun getPluginName(): String? {
        return "Lands"
    }
}