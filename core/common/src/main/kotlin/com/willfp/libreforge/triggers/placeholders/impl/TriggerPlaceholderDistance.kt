package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderDistance : TriggerPlaceholder("distance") {
    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val victim = data.victim ?: return emptyList()
        val player = data.player ?: return emptyList()

        return listOf(
            NamedValue(
                "distance",
                player.location.toVector().distance(victim.location.toVector())
            )
        )
    }
}
