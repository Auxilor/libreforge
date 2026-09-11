package com.willfp.libreforge.integrations.luckperms

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpCanDemote
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpCanPromote
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpGroupCountAbove
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpGroupWeightAbove
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpHasContext
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpHasMeta
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpHasTemporaryNode
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpInGroup
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpInTrack
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpMetaAbove
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpMetaBelow
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpMetaEquals
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpNodeExpiresWithin
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpPermissionTristate
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpPrefixIs
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpSuffixIs
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpTrackPositionAbove
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpTrackPositionBelow
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpWeightAbove
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpWeightBelow
import com.willfp.libreforge.integrations.luckperms.impl.ConditionLpWeightEquals
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpAddGroup
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpAddPermission
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpClearNodes
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpDemote
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpLogAction
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpPromote
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpPushUserUpdate
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpRemoveGroup
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpRemoveMeta
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpRemovePermission
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpSendCustomMessage
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpSetMeta
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpSetPrefix
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpSetPrimaryGroup
import com.willfp.libreforge.integrations.luckperms.impl.EffectLpSetSuffix
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpCause
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpChannel
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpContext
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpDataType
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpGroup
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpIsNegated
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpIsTemporary
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpNodeType
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpPermission
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpSourceType
import com.willfp.libreforge.integrations.luckperms.impl.FilterLpTrack
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToGroupDisplayName
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToMeta
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToPrefix
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToPrimaryGroup
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToSuffix
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToTrackNextGroup
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpTextToTrackPreviousGroup
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpValueToGroupCount
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpValueToMeta
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpValueToTrackPosition
import com.willfp.libreforge.integrations.luckperms.impl.MutatorLpValueToWeight
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpConfigReload
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpContextUpdate
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpCustomMessage
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpDemote
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpFirstLogin
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupCreate
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupDelete
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpGroupRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpLogNotify
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpMetaChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeClear
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpNodeRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPermissionAdd
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPermissionRemove
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPostSync
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPrefixChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpPromote
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpSuffixChange
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpTrackAddGroup
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpTrackRemoveGroup
import com.willfp.libreforge.integrations.luckperms.impl.TriggerLpUserLoad
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.triggers.Triggers

object LuckPermsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerLpPromote)
        Triggers.register(TriggerLpDemote)
        Triggers.register(TriggerLpGroupAdd)
        Triggers.register(TriggerLpGroupRemove)
        Triggers.register(TriggerLpPermissionAdd)
        Triggers.register(TriggerLpPermissionRemove)
        Triggers.register(TriggerLpMetaChange)
        Triggers.register(TriggerLpPrefixChange)
        Triggers.register(TriggerLpSuffixChange)
        Triggers.register(TriggerLpNodeAdd)
        Triggers.register(TriggerLpNodeRemove)
        Triggers.register(TriggerLpNodeClear)
        Triggers.register(TriggerLpFirstLogin)
        Triggers.register(TriggerLpUserLoad)
        Triggers.register(TriggerLpContextUpdate)
        Triggers.register(TriggerLpCustomMessage)
        Triggers.register(TriggerLpGroupCreate)
        Triggers.register(TriggerLpGroupDelete)
        Triggers.register(TriggerLpTrackAddGroup)
        Triggers.register(TriggerLpTrackRemoveGroup)
        Triggers.register(TriggerLpConfigReload)
        Triggers.register(TriggerLpPostSync)
        Triggers.register(TriggerLpLogNotify)

        Conditions.register(ConditionLpInGroup)
        Conditions.register(ConditionLpGroupCountAbove)
        Conditions.register(ConditionLpPermissionTristate)
        Conditions.register(ConditionLpHasMeta)
        Conditions.register(ConditionLpMetaEquals)
        Conditions.register(ConditionLpMetaAbove)
        Conditions.register(ConditionLpMetaBelow)
        Conditions.register(ConditionLpPrefixIs)
        Conditions.register(ConditionLpSuffixIs)
        Conditions.register(ConditionLpWeightAbove)
        Conditions.register(ConditionLpWeightBelow)
        Conditions.register(ConditionLpWeightEquals)
        Conditions.register(ConditionLpGroupWeightAbove)
        Conditions.register(ConditionLpInTrack)
        Conditions.register(ConditionLpTrackPositionAbove)
        Conditions.register(ConditionLpTrackPositionBelow)
        Conditions.register(ConditionLpCanPromote)
        Conditions.register(ConditionLpCanDemote)
        Conditions.register(ConditionLpHasContext)
        Conditions.register(ConditionLpHasTemporaryNode)
        Conditions.register(ConditionLpNodeExpiresWithin)

        Effects.register(EffectLpAddGroup)
        Effects.register(EffectLpRemoveGroup)
        Effects.register(EffectLpSetPrimaryGroup)
        Effects.register(EffectLpAddPermission)
        Effects.register(EffectLpRemovePermission)
        Effects.register(EffectLpClearNodes)
        Effects.register(EffectLpSetMeta)
        Effects.register(EffectLpRemoveMeta)
        Effects.register(EffectLpSetPrefix)
        Effects.register(EffectLpSetSuffix)
        Effects.register(EffectLpPromote)
        Effects.register(EffectLpDemote)
        Effects.register(EffectLpSendCustomMessage)
        Effects.register(EffectLpPushUserUpdate)
        Effects.register(EffectLpLogAction)

        Filters.register(FilterLpGroup)
        Filters.register(FilterLpTrack)
        Filters.register(FilterLpNodeType)
        Filters.register(FilterLpPermission)
        Filters.register(FilterLpContext)
        Filters.register(FilterLpCause)
        Filters.register(FilterLpSourceType)
        Filters.register(FilterLpChannel)
        Filters.register(FilterLpDataType)
        Filters.register(FilterLpIsTemporary)
        Filters.register(FilterLpIsNegated)

        Mutators.register(MutatorLpTextToPrefix)
        Mutators.register(MutatorLpTextToSuffix)
        Mutators.register(MutatorLpTextToPrimaryGroup)
        Mutators.register(MutatorLpTextToGroupDisplayName)
        Mutators.register(MutatorLpTextToMeta)
        Mutators.register(MutatorLpValueToMeta)
        Mutators.register(MutatorLpValueToWeight)
        Mutators.register(MutatorLpValueToGroupCount)
        Mutators.register(MutatorLpValueToTrackPosition)
        Mutators.register(MutatorLpTextToTrackNextGroup)
        Mutators.register(MutatorLpTextToTrackPreviousGroup)

        val luckPerms = LuckPermsManager.luckPerms ?: return

        LuckPermsEventListener.register(luckPerms, plugin)
    }

    override fun getPluginName(): String {
        return "LuckPerms"
    }
}
