package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.providedActiveEffects

object ConditionHasActiveEffect : Condition<NoCompileData>("has_active_effect") {
    override val description = "Passes when the dispatcher has an active effect block that runs one of the specified effects."
    override val categories = setOf("meta")
    override val additionalInfo = listOf(
        "An effect block is active when its holder is held and its conditions are met, whether or not it has triggers.",
        "Permanent effects that use this condition update on the next holder refresh, about once a second."
    )

    override val arguments = arguments {
        require(
            listOf("effect", "effects"),
            "You must specify the effect!",
            description = "The effect ID(s) to check for.",
            type = ArgType.STRING_LIST,
            example = listOf("xp_multiplier", "damage_multiplier")
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val effectIds = buildSet {
            config.getStringOrNull("effect")?.takeIf { it.isNotBlank() }?.let { add(it.lowercase()) }
            config.getStrings("effects").filter { it.isNotBlank() }.mapTo(this) { it.lowercase() }
        }

        if (effectIds.isEmpty()) {
            return false
        }

        return dispatcher.providedActiveEffects.any { provided ->
            provided.effect.effects.any { it.effect.id in effectIds }
        }
    }
}
