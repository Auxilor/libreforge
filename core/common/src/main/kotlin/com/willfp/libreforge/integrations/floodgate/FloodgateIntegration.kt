package com.willfp.libreforge.integrations.floodgate

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.floodgate.impl.ConditionBedrockDeviceOs
import com.willfp.libreforge.integrations.floodgate.impl.ConditionBedrockInputMode
import com.willfp.libreforge.integrations.floodgate.impl.ConditionBedrockUiProfile
import com.willfp.libreforge.integrations.floodgate.impl.ConditionBedrockVersion
import com.willfp.libreforge.integrations.floodgate.impl.ConditionHasLinkedJavaAccount
import com.willfp.libreforge.integrations.floodgate.impl.ConditionIsBedrockPlayer
import com.willfp.libreforge.integrations.floodgate.impl.EffectCloseBedrockForm
import com.willfp.libreforge.integrations.floodgate.impl.EffectSendBedrockCustomForm
import com.willfp.libreforge.integrations.floodgate.impl.EffectSendBedrockForm
import com.willfp.libreforge.integrations.floodgate.impl.EffectSendBedrockModalForm
import com.willfp.libreforge.integrations.floodgate.impl.EffectTransferBedrockPlayer
import com.willfp.libreforge.integrations.floodgate.impl.FilterBedrockDeviceOs
import com.willfp.libreforge.integrations.floodgate.impl.FilterBedrockFormId
import com.willfp.libreforge.integrations.floodgate.impl.FilterBedrockInputMode
import com.willfp.libreforge.integrations.floodgate.impl.FilterBedrockUiProfile
import com.willfp.libreforge.integrations.floodgate.impl.FilterIsBedrockPlayer
import com.willfp.libreforge.integrations.floodgate.impl.FilterIsLinkedBedrockPlayer
import com.willfp.libreforge.integrations.floodgate.impl.FilterVictimIsBedrockPlayer
import com.willfp.libreforge.integrations.floodgate.impl.MutatorBedrockGamertagAsText
import com.willfp.libreforge.integrations.floodgate.impl.MutatorBedrockPlayerCountAsValue
import com.willfp.libreforge.integrations.floodgate.impl.MutatorBedrockXuidAsText
import com.willfp.libreforge.integrations.floodgate.impl.MutatorLinkedJavaNameAsText
import com.willfp.libreforge.integrations.floodgate.impl.TriggerBedrockFormClosed
import com.willfp.libreforge.integrations.floodgate.impl.TriggerBedrockFormResponse
import com.willfp.libreforge.integrations.floodgate.impl.TriggerBedrockJoin
import com.willfp.libreforge.integrations.floodgate.impl.TriggerJavaJoin
import com.willfp.libreforge.mutators.Mutators
import com.willfp.libreforge.triggers.Triggers

object FloodgateIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionIsBedrockPlayer)
        Conditions.register(ConditionBedrockDeviceOs)
        Conditions.register(ConditionBedrockInputMode)
        Conditions.register(ConditionBedrockUiProfile)
        Conditions.register(ConditionBedrockVersion)
        Conditions.register(ConditionHasLinkedJavaAccount)

        Filters.register(FilterIsBedrockPlayer)
        Filters.register(FilterVictimIsBedrockPlayer)
        Filters.register(FilterIsLinkedBedrockPlayer)
        Filters.register(FilterBedrockDeviceOs)
        Filters.register(FilterBedrockInputMode)
        Filters.register(FilterBedrockUiProfile)

        Filters.register(FilterBedrockFormId)

        Effects.register(EffectSendBedrockForm)
        Effects.register(EffectSendBedrockModalForm)
        Effects.register(EffectSendBedrockCustomForm)
        Effects.register(EffectCloseBedrockForm)
        Effects.register(EffectTransferBedrockPlayer)

        Mutators.register(MutatorBedrockGamertagAsText)
        Mutators.register(MutatorBedrockXuidAsText)
        Mutators.register(MutatorLinkedJavaNameAsText)
        Mutators.register(MutatorBedrockPlayerCountAsValue)

        Triggers.register(TriggerBedrockJoin)
        Triggers.register(TriggerJavaJoin)
        Triggers.register(TriggerBedrockFormResponse)
        Triggers.register(TriggerBedrockFormClosed)
    }

    override fun getPluginName(): String {
        return "floodgate"
    }
}
