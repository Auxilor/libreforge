package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder
import org.bukkit.attribute.Attribute

object TriggerPlaceholderPlayer : TriggerPlaceholder("player") {
    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val player = data.player ?: return emptyList()

        return listOf(
            NamedValue(
                "player",
                player.name
            ),
            NamedValue(
                "player_uuid",
                player.uniqueId
            )
        )
    }
}
