package com.willfp.libreforge.integrations.luckperms.impl.meta

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerLuckPermsMetaChange : Trigger("meta_changed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    fun dispatch(player: Player, key: String, value: String) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = "$key:$value"
            )
        )
    }
}
