package com.willfp.libreforge.integrations.cmi

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiBalanceAbove
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiHasRank
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiIsAfk
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiIsGod
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiIsJailed
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiIsMuted
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiIsVanished
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiKitAvailable
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiPlaytimeAbove
import com.willfp.libreforge.integrations.cmi.impl.ConditionCmiStatisticAbove
import com.willfp.libreforge.integrations.cmi.impl.EffectCmiGiveBalance
import com.willfp.libreforge.integrations.cmi.impl.EffectCmiGiveKit
import com.willfp.libreforge.integrations.cmi.impl.EffectCmiSetBalance
import com.willfp.libreforge.integrations.cmi.impl.EffectCmiTakeBalance
import com.willfp.libreforge.integrations.cmi.impl.EffectCmiTeleportToWarp
import com.willfp.libreforge.integrations.cmi.impl.FilterCmiKit
import com.willfp.libreforge.integrations.cmi.impl.FilterCmiPortal
import com.willfp.libreforge.integrations.cmi.impl.FilterCmiSellType
import com.willfp.libreforge.integrations.cmi.impl.FilterCmiWarp
import com.willfp.libreforge.integrations.cmi.impl.MutatorCmiLocationToHome
import com.willfp.libreforge.integrations.cmi.impl.MutatorCmiLocationToWarp
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiAfkEnter
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiAfkLeave
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiBalanceChange
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiChequeCreate
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiChequeUse
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiHomeCreate
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiHomeRemove
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiJail
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiKitAcquire
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiNicknameChange
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiPortalUse
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiSellItems
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiSit
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiTeleportRequest
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiUnjail
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiUnvanish
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiVanish
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiWarn
import com.willfp.libreforge.integrations.cmi.impl.TriggerCmiWarpUse
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.triggers.Triggers

object CMIIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerCmiBalanceChange)
        Triggers.register(TriggerCmiChequeCreate)
        Triggers.register(TriggerCmiChequeUse)
        Triggers.register(TriggerCmiSellItems)
        Triggers.register(TriggerCmiAfkEnter)
        Triggers.register(TriggerCmiAfkLeave)
        Triggers.register(TriggerCmiVanish)
        Triggers.register(TriggerCmiUnvanish)
        Triggers.register(TriggerCmiJail)
        Triggers.register(TriggerCmiUnjail)
        Triggers.register(TriggerCmiNicknameChange)
        Triggers.register(TriggerCmiWarn)
        Triggers.register(TriggerCmiSit)
        Triggers.register(TriggerCmiHomeCreate)
        Triggers.register(TriggerCmiHomeRemove)
        Triggers.register(TriggerCmiWarpUse)
        Triggers.register(TriggerCmiPortalUse)
        Triggers.register(TriggerCmiTeleportRequest)
        Triggers.register(TriggerCmiKitAcquire)

        Conditions.register(ConditionCmiIsAfk)
        Conditions.register(ConditionCmiIsVanished)
        Conditions.register(ConditionCmiIsJailed)
        Conditions.register(ConditionCmiIsMuted)
        Conditions.register(ConditionCmiIsGod)
        Conditions.register(ConditionCmiHasRank)
        Conditions.register(ConditionCmiPlaytimeAbove)
        Conditions.register(ConditionCmiBalanceAbove)
        Conditions.register(ConditionCmiKitAvailable)
        Conditions.register(ConditionCmiStatisticAbove)

        Effects.register(EffectCmiGiveBalance)
        Effects.register(EffectCmiTakeBalance)
        Effects.register(EffectCmiSetBalance)
        Effects.register(EffectCmiGiveKit)
        Effects.register(EffectCmiTeleportToWarp)

        Filters.register(FilterCmiWarp)
        Filters.register(FilterCmiPortal)
        Filters.register(FilterCmiKit)
        Filters.register(FilterCmiSellType)

        Mutators.register(MutatorCmiLocationToWarp)
        Mutators.register(MutatorCmiLocationToHome)
    }

    override fun getPluginName(): String {
        return "CMI"
    }
}
