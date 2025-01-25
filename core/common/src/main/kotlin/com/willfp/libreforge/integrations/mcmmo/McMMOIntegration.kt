package com.willfp.libreforge.integrations.mcmmo

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.integrations.LoadableIntegration
import com.willfp.libreforge.integrations.mcmmo.impl.ConditionHasMcMMOLevel
import com.willfp.libreforge.integrations.mcmmo.impl.ConditionMcMMOAbilityOnCooldown
import com.willfp.libreforge.integrations.mcmmo.impl.EffectGiveMcMMOXp
import com.willfp.libreforge.integrations.mcmmo.impl.EffectMcMMOXpMultiplier
import com.willfp.libreforge.integrations.mcmmo.impl.FilterMcMMOAbility
import com.willfp.libreforge.integrations.mcmmo.impl.FilterMcMMOSkill
import com.willfp.libreforge.integrations.mcmmo.impl.TriggerGainMcMMOXp
import com.willfp.libreforge.integrations.mcmmo.impl.TriggerLevelDownMcMMO
import com.willfp.libreforge.integrations.mcmmo.impl.TriggerLevelUpMcMMO
import com.willfp.libreforge.integrations.mcmmo.impl.TriggerMcMMOAbilityActivate
import com.willfp.libreforge.integrations.mcmmo.impl.TriggerMcMMOAbilityDeactivate
import com.willfp.libreforge.triggers.Triggers

object McMMOIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        Effects.register(EffectMcMMOXpMultiplier)
        Effects.register(EffectGiveMcMMOXp)
        Triggers.register(TriggerGainMcMMOXp)
        Triggers.register(TriggerLevelUpMcMMO)
        Triggers.register(TriggerLevelDownMcMMO)
        Triggers.register(TriggerMcMMOAbilityActivate)
        Triggers.register(TriggerMcMMOAbilityDeactivate)
        Filters.register(FilterMcMMOSkill)
        Filters.register(FilterMcMMOAbility)
        Conditions.register(ConditionHasMcMMOLevel)
        Conditions.register(ConditionMcMMOAbilityOnCooldown)
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}
