package com.willfp.libreforge.integrations.smartpets.impl

import com.smartpets.events.PetExperienceGainEvent
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.MultiMultiplierEffect
import com.willfp.libreforge.integrations.smartpets.owner
import com.willfp.libreforge.toDispatcher
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import kotlin.math.roundToInt

object EffectSpXpMultiplier : MultiMultiplierEffect<PetExperienceGainEvent.Source>("sp_xp_multiplier") {
    override val description = "Multiplies the experience the player's SmartPets pets gain, for one or all sources."
    override val categories = setOf("pets")
    override val additionalInfo = listOf(
        "Requires SmartPets Pro to be installed.",
        "Sources: FEED, PET, PLAY, FOLLOW, DEFEND, ASSIST, GUARD, SCOUT, HUNT, PERCH, MIMIC, PLUGIN."
    )

    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The experience multiplier. Supports expressions.",
            type = ArgType.EXPRESSION
        )
        optional(
            "sources",
            description = "The experience sources to multiply. If omitted, applies to all sources.",
            type = ArgType.STRING_LIST
        )
    }

    override val key = "sources"

    override fun getElement(key: String): PetExperienceGainEvent.Source? {
        return PetExperienceGainEvent.Source.entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
    }

    override fun getAllElements(): Collection<PetExperienceGainEvent.Source> {
        return PetExperienceGainEvent.Source.entries
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun handle(event: PetExperienceGainEvent) {
        val player = event.owner ?: return

        event.amount = (event.amount * getMultiplier(player.toDispatcher(), event.source)).roundToInt()
    }
}
