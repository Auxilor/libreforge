package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderDistance : TriggerPlaceholder("distance") {
    override fun createPlaceholders(trigger: DispatchedTrigger): Collection<NamedValue> {
        val victim = trigger.data.victim ?: return emptyList()
        val player = trigger.data.player ?: return emptyList()

        return listOf(
            NamedValue(
                "distance",
                player.location.toVector().distance(victim.location.toVector())
            )
        )
    }
}
