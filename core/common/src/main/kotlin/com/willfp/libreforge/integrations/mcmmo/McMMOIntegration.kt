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
        Conditions.register(ConditionHasMcMMOLevel)
        Conditions.register(ConditionMcMMOAbilityOnCooldown)
        Effects.register(EffectGiveMcMMOXp)
        Effects.register(EffectMcMMOXpMultiplier)
        Filters.register(FilterMcMMOAbility)
        Filters.register(FilterMcMMOSkill)
        Triggers.register(TriggerGainMcMMOXp)
        Triggers.register(TriggerLevelDownMcMMO)
        Triggers.register(TriggerLevelUpMcMMO)
        Triggers.register(TriggerMcMMOAbilityActivate)
        Triggers.register(TriggerMcMMOAbilityDeactivate)
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }
}
