package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.registry.NamespacedId

object EffectGiveSkillXpNaturally : Effect<NoCompileData>("give_skill_xp_naturally") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("amount", "You must specify the amount to add / subtract!")
        require("skill", "You must specify the skill to give XP to!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val auraSkills = AuraSkillsApi.get()
        val user = auraSkills.getUser(player.uniqueId)

        val skillId = NamespacedId.fromDefault(config.getString("skill"))
        val skill = auraSkills.globalRegistry.getSkill(skillId) ?: return false

        user.addSkillXp(skill, config.getDoubleFromExpression("amount", data))

        return true
    }
}