package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder
import org.bukkit.attribute.Attribute

object TriggerPlaceholderVictim : TriggerPlaceholder("victim") {
    override fun createPlaceholders(trigger: DispatchedTrigger): Collection<NamedValue> {
        val victim = trigger.data.victim ?: return emptyList()

        return listOf(
            NamedValue(
                "victim_health",
                victim.health
            ),
            NamedValue(
                "victim_max_health",
                victim.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 0.0
            )
        )
    }
}
