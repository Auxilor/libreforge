package com.willfp.libreforge.integrations.edprisoncore

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.edprisoncore.impl.ConditionHasEdPrisonCurrency
import com.willfp.libreforge.integrations.edprisoncore.impl.ConditionHasEdPrisonRobot
import com.willfp.libreforge.integrations.edprisoncore.impl.ConditionInEdPrisonGang
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectCreateEdPrisonExplosion
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectGiveEdPrisonEconomy
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectGiveEdPrisonPouch
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectMultiplyEdPrisonEconomy
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectSetEdPrisonEconomy
import com.willfp.libreforge.integrations.edprisoncore.impl.EffectUpdateEdPrisonPickaxe

object EdPrisonCoreIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Conditions.register(ConditionHasEdPrisonCurrency) // Working
        Conditions.register(ConditionHasEdPrisonRobot) // Working
        Conditions.register(ConditionInEdPrisonGang) // Working
        Effects.register(EffectCreateEdPrisonExplosion) // Partially working, needs further testing
        Effects.register(EffectGiveEdPrisonEconomy) // Working
        Effects.register(EffectGiveEdPrisonPouch) // Working
        Effects.register(EffectMultiplyEdPrisonEconomy) // No good
        Effects.register(EffectSetEdPrisonEconomy) // Working
        Effects.register(EffectUpdateEdPrisonPickaxe) // Unsure, needs further testing
    }

    override fun getPluginName(): String {
        return "EdPrison"
    }
}