package com.willfp.libreforge.integrations.aureliumskills

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArguments
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.aureliumskills.impl.ArgumentManaCost
import com.willfp.libreforge.integrations.aureliumskills.impl.ConditionHasMana
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectAddStat
import com.willfp.libreforge.integrations.aureliumskills.impl.EffectSkillXpMultiplier

@Deprecated("AureliumSkills integration is deprecated and will be removed in the future. Update to AuraSkills instead!")
object AureliumSkillsIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        if (plugin.server.pluginManager.getPlugin("EcoSkills") != null) {
            return
        }

        Conditions.register(ConditionHasMana)
        EffectArguments.register(ArgumentManaCost)
        Effects.register(EffectAddStat)
        Effects.register(EffectSkillXpMultiplier)
    }

    override fun getPluginName(): String {
        return "AureliumSkills"
    }
}
