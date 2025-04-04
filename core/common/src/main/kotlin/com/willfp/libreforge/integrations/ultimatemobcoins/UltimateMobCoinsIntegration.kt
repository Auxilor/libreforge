package com.willfp.libreforge.integrations.ultimatemobcoins

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.ultimatemobcoins.impl.EffectMobCoinsMultiplier

object UltimateMobCoinsIntegration: LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectMobCoinsMultiplier)
    }

    override fun getPluginName(): String = "UltimateMobCoins"
}