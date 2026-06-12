package com.willfp.libreforge.integrations.factionsbridge

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionFactionPowerAbove
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionFactionPowerBelow
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionFactionRelationIs
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionFactionRoleAtLeast
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionHasFaction
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionInFactionTerritory
import com.willfp.libreforge.integrations.factionsbridge.impl.conditions.ConditionIsFactionRole
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionAddBank
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionAddPoints
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionAddPower
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionAddStrike
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionAddTnt
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionRemoveStrike
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionRemoveTnt
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionSetBank
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionSetPoints
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionSetPower
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectFactionSetTitle
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectPlayerAddPower
import com.willfp.libreforge.integrations.factionsbridge.impl.effects.EffectPlayerSetPower
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterDisbandReason
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterExecutorOnly
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterFactionRelation
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterFactionRole
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterFactionRoleAtLeast
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterLeaveReason
import com.willfp.libreforge.integrations.factionsbridge.impl.filters.FilterOwnerOnly
import com.willfp.libreforge.integrations.factionsbridge.impl.mutators.MutatorFactionLeaderAsPlayer
import com.willfp.libreforge.integrations.factionsbridge.impl.mutators.MutatorVictimFactionLeaderAsPlayer
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionClaim
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionCreate
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionDisband
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionJoin
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionLeave
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionRename
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionUnclaim
import com.willfp.libreforge.integrations.factionsbridge.impl.triggers.TriggerFactionUnclaimAll
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.triggers.Triggers

object FactionsBridgeIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        val api = FactionsBridge.getFactionsAPI() ?: return
        if (!api.hasRegistered()) return

        Triggers.register(TriggerFactionCreate)
        Triggers.register(TriggerFactionJoin)
        Triggers.register(TriggerFactionLeave)
        Triggers.register(TriggerFactionClaim)
        Triggers.register(TriggerFactionUnclaim)
        Triggers.register(TriggerFactionRename)
        Triggers.register(TriggerFactionDisband)
        Triggers.register(TriggerFactionUnclaimAll)

        Effects.register(EffectFactionAddPower)
        Effects.register(EffectFactionSetPower)
        Effects.register(EffectPlayerAddPower)
        Effects.register(EffectPlayerSetPower)
        Effects.register(EffectFactionAddBank)
        Effects.register(EffectFactionSetBank)
        Effects.register(EffectFactionAddTnt)
        Effects.register(EffectFactionRemoveTnt)
        Effects.register(EffectFactionAddPoints)
        Effects.register(EffectFactionSetPoints)
        Effects.register(EffectFactionSetTitle)
        Effects.register(EffectFactionAddStrike)
        Effects.register(EffectFactionRemoveStrike)

        Conditions.register(ConditionHasFaction)
        Conditions.register(ConditionFactionPowerAbove)
        Conditions.register(ConditionFactionPowerBelow)
        Conditions.register(ConditionIsFactionRole)
        Conditions.register(ConditionFactionRoleAtLeast)
        Conditions.register(ConditionInFactionTerritory)
        Conditions.register(ConditionFactionRelationIs)

        Filters.register(FilterLeaveReason)
        Filters.register(FilterDisbandReason)
        Filters.register(FilterFactionRole)
        Filters.register(FilterFactionRoleAtLeast)
        Filters.register(FilterOwnerOnly)
        Filters.register(FilterExecutorOnly)
        Filters.register(FilterFactionRelation)

        Mutators.register(MutatorFactionLeaderAsPlayer)
        Mutators.register(MutatorVictimFactionLeaderAsPlayer)
    }

    override fun getPluginName() = "FactionsBridge"
}
