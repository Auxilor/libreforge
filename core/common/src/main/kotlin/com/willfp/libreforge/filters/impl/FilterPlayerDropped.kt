package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.DropProvenanceListener
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData

object FilterPlayerDropped : Filter<NoCompileData, Boolean>("player_dropped") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val item = data.itemEntity ?: return !value
        val player = data.player ?: return !value
        val dropperUuid = item.getMetadata(DropProvenanceListener.DROPPER_KEY)
            .firstOrNull()?.asString()
        val isDroppedByThisPlayer = dropperUuid == player.uniqueId.toString()
        return isDroppedByThisPlayer == value
    }
}
