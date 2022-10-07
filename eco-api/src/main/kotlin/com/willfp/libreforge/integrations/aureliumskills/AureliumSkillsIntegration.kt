package com.willfp.libreforge.integrations.aureliumskills

import com.willfp.eco.core.integrations.Integration
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.effects.Effect

object AureliumSkillsIntegration : Integration {
    private lateinit var ADD_STAT: Effect
    private lateinit var HAS_MANA: Condition
    private lateinit var SKILL_XP_MULTIPLIER: Effect

    fun load() {
        ADD_STAT = EffectAddStat()
        HAS_MANA = ConditionHasMana()
        SKILL_XP_MULTIPLIER = EffectSkillXpMultiplier()
        LibReforgePlugin.instance.eventManager.registerListener(UseManaHandler())
    }

    override fun getPluginName(): String {
        return "AureliumSkills"
    }
}