package com.willfp.libreforge.integrations.mcmmo.impl

import com.gmail.nossr50.datatypes.skills.PrimarySkillType
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent
import com.gmail.nossr50.mcMMO
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler

object EffectMcMMOXpMultiplier : MultiMultiplierEffect<PrimarySkillType>("mcmmo_xp_multiplier") {
    override val description = "Multiplies McMMO XP gained for one or all skills while the holder is active."
    override val categories = setOf("economy")

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The XP multiplier. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "skills",
            description = "List of McMMO skill names to apply the multiplier to. If omitted, applies to all skills.",
            type = ArgType.STRING_LIST
        )
    }

    override val key = "skills"

    override fun getElement(key: String): PrimarySkillType? {
        return mcMMO.p.skillTools.matchSkill(key)
    }

    override fun getAllElements(): Collection<PrimarySkillType> {
        return PrimarySkillType.entries
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: McMMOPlayerXpGainEvent) {
        val player = event.player

        event.rawXpGained *= getMultiplier(player.toDispatcher(), event.skill).toFloat()
    }
}
