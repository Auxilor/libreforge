package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.enumValueOfOrNull
import com.willfp.libreforge.get
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.updateEffects
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.skill.SkillLevelUpEvent
import dev.aurelium.auraskills.api.skill.Skills
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority

object ConditionHasSkillLevel : Condition<NoCompileData>("has_skill_level") {
    override val description = "Passes when the player has reached at least the specified level in a given AuraSkills skill."
    override val categories = setOf("player", "economy")
    override val additionalInfo = listOf("Requires the AuraSkills plugin.")

    override val arguments = arguments {
        require(
            "skill",
            "You must specify the skill!",
            description = "The AuraSkills skill to check (e.g. FARMING, MINING).",
            type = ArgType.STRING
        )
        require(
            "level",
            "You must specify the level!",
            description = "The minimum skill level the player must have.",
            type = ArgType.EXPRESSION
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val skill = enumValueOfOrNull<Skills>(config.getString("skill").uppercase()) ?: return false

        return AuraSkillsApi.get().getUser(player.uniqueId).getSkillLevel(skill) >= config.getIntFromExpression(
            "level",
            player
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: SkillLevelUpEvent) {
        event.player.toDispatcher().updateEffects()
    }
}
