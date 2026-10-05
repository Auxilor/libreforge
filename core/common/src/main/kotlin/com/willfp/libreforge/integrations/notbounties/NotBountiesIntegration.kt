package com.willfp.libreforge.integrations.notbounties

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.notbounties.impl.ConditionNbBountyAbove
import com.willfp.libreforge.integrations.notbounties.impl.ConditionNbBountyBelow
import com.willfp.libreforge.integrations.notbounties.impl.ConditionNbHasBounty
import com.willfp.libreforge.integrations.notbounties.impl.EffectNbAddBounty
import com.willfp.libreforge.integrations.notbounties.impl.TriggerNbBountyClaimed
import com.willfp.libreforge.integrations.notbounties.impl.TriggerNbBuyBounty
import com.willfp.libreforge.integrations.notbounties.impl.TriggerNbClaimBounty
import com.willfp.libreforge.integrations.notbounties.impl.TriggerNbReceiveBounty
import com.willfp.libreforge.integrations.notbounties.impl.TriggerNbSetBounty
import com.willfp.libreforge.triggers.Triggers

object NotBountiesIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Triggers.register(TriggerNbSetBounty)
        Triggers.register(TriggerNbReceiveBounty)
        Triggers.register(TriggerNbClaimBounty)
        Triggers.register(TriggerNbBountyClaimed)
        Triggers.register(TriggerNbBuyBounty)

        Conditions.register(ConditionNbHasBounty)
        Conditions.register(ConditionNbBountyAbove)
        Conditions.register(ConditionNbBountyBelow)

        Effects.register(EffectNbAddBounty)
    }

    override fun getPluginName(): String {
        return "NotBounties"
    }
}
