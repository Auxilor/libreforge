package com.willfp.libreforge.integrations.ultimatemobcoins

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectMobCoinsChanceMultiplier
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectMobCoinsDropMultiplier

object UltimateMobCoinsIntegration: LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectMobCoinsDropMultiplier)
        Effects.register(EffectMobCoinsChanceMultiplier)
    }

    override fun getPluginName(): String = "UltimateMobCoins"
}