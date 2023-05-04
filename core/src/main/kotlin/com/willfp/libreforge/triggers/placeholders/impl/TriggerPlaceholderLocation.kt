package com.willfp.libreforge.triggers.placeholders.impl

import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.placeholders.TriggerPlaceholder

object TriggerPlaceholderLocation : TriggerPlaceholder("location") {
    override fun createPlaceholders(data: TriggerData): Collection<NamedValue> {
        val location = data.location ?: return emptyList()

        return listOf(
            NamedValue(
                listOf("location_x", "loc_x", "x"),
                location.x
            ),
            NamedValue(
                listOf("location_block_x", "loc_b_x", "block_x", "bx"),
                location.blockX
            ),
            NamedValue(
                listOf("location_y", "loc_y", "y"),
                location.y
            ),
            NamedValue(
                listOf("location_block_y", "loc_b_y", "block_y", "by"),
                location.blockY
            ),
            NamedValue(
                listOf("location_z", "loc_z", "z"),
                location.z
            ),
            NamedValue(
                listOf("location_block_z", "loc_b_z", "block_z", "bz"),
                location.blockZ
            ),
            NamedValue(
                listOf("location_world", "loc_w", "world"),
                location.world?.name ?: ""
            )
        )
    }
}
