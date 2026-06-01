package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.event.skill.XpGainEvent
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.skill.Skill
import org.bukkit.event.EventHandler

object EffectSkillXpMultiplier : MultiMultiplierEffect<Skill>("skill_xp_multiplier") {
    override val description = "Multiplies AuraSkills XP gained for one or all skills while the holder is active."
    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The XP multiplier. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "skills",
            description = "List of AuraSkills skill IDs to apply the multiplier to. If omitted, applies to all skills.",
            type = ArgType.STRING_LIST
        )
    }

    override val key = "skills"

    override fun getElement(key: String): Skill {
        return AuraSkillsApi.get().globalRegistry.getSkill(NamespacedId.fromDefault(key))
            ?: throw IllegalArgumentException("Unknown skill $key")
    }

    override fun getAllElements(): Collection<Skill> {
        return AuraSkillsApi.get().globalRegistry.skills
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: XpGainEvent) {
        val player = event.player

        val multiplier = getMultiplier(player.toDispatcher(), event.skill)

        event.amount *= multiplier
    }
}
