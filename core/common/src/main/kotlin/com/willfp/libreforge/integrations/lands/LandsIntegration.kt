package com.willfp.libreforge.integrations.lands

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.lands.impl.ConditionHasLandsRole
import com.willfp.libreforge.integrations.lands.impl.ConditionInTrustedLand
import com.willfp.libreforge.integrations.lands.impl.TriggerClaimLand
import com.willfp.libreforge.integrations.lands.impl.TriggerJoinLand
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankDeposit
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsBankWithdraw
import com.willfp.libreforge.integrations.lands.impl.TriggerLandsSpawnTeleport
import com.willfp.libreforge.integrations.lands.impl.TriggerLeaveLand
import com.willfp.libreforge.integrations.lands.impl.TriggerUnclaimLand
import com.willfp.libreforge.triggers.Triggers

object LandsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasLandsRole)
        Conditions.register(ConditionInTrustedLand)
        Triggers.register(TriggerClaimLand)
        Triggers.register(TriggerJoinLand)
        Triggers.register(TriggerLandsBankDeposit)
        Triggers.register(TriggerLandsBankWithdraw)
        Triggers.register(TriggerLandsSpawnTeleport)
        Triggers.register(TriggerLeaveLand)
        Triggers.register(TriggerUnclaimLand)
    }

    override fun getPluginName(): String? {
        return "Lands"
    }
}