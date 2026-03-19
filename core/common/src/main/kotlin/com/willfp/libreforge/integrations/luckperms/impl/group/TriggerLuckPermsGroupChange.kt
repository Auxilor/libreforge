package com.willfp.libreforge.integrations.luckperms.impl.group

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerLuckPermsGroupChange : Trigger("group_changed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    fun dispatch(player: Player, group: String) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = group
            )
        )
    }
}
