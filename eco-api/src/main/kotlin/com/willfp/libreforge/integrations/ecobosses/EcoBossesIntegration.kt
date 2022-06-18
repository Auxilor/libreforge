package com.willfp.libreforge.integrations.ecobosses

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.Trigger

object EcoBossesIntegration: Integration {
    private lateinit var BOSS_DROP_CHANCE_MULTIPLIER: Effect
    private lateinit var KILL_BOSS: Trigger
    private lateinit var SPAWN_BOSS: Trigger

    fun load() {
        BOSS_DROP_CHANCE_MULTIPLIER = EffectBossDropChanceMultiplier()
        KILL_BOSS = TriggerKillBoss()
        SPAWN_BOSS = TriggerSpawnBoss()
    }

    override fun getPluginName(): String {
        return "EcoBosses"
    }
}
