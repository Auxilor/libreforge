package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ecobosses.impl.EffectBossDropChanceMultiplier
import com.willfp.libreforge.integrations.ecobosses.impl.TriggerKillBoss
import com.willfp.libreforge.integrations.ecobosses.impl.TriggerSpawnBoss
import com.willfp.libreforge.triggers.Triggers

object EcoBossesIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectBossDropChanceMultiplier)
        Triggers.register(TriggerKillBoss)
        Triggers.register(TriggerSpawnBoss)
    }

    override fun getPluginName(): String {
        return "EcoBosses"
    }
}
