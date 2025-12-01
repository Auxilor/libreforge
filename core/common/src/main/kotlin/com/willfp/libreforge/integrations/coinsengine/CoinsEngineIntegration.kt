package com.willfp.libreforge.integrations.coinsengine

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.coinsengine.impl.ConditionHasCurrency
import com.willfp.libreforge.integrations.coinsengine.impl.EffectGiveCurrency
import com.willfp.libreforge.integrations.coinsengine.impl.EffectSetCurrency
import com.willfp.libreforge.integrations.coinsengine.impl.EffectTakeCurrency

object CoinsEngineIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectGiveCurrency)
        Effects.register(EffectTakeCurrency)
        Effects.register(EffectSetCurrency)
        Conditions.register(ConditionHasCurrency)
    }

    override fun getPluginName(): String {
        return "CoinsEngine"
    }
}