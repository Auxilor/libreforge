package com.willfp.libreforge.integrations.ecobosses

import com.willfp.libreforge.effects.Effect

object EcoBossesIntegration {
    private lateinit var BOSS_DROP_CHANCE_MULTIPLIER: Effect

    fun load() {
        BOSS_DROP_CHANCE_MULTIPLIER = EffectBossDropChanceMultiplier()
    }
}
