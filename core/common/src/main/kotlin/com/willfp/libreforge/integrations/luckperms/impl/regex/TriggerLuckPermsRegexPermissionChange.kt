package com.willfp.libreforge.integrations.luckperms.impl.regex

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.entity.Player

object TriggerLuckPermsRegexPermissionChange : Trigger("regex_permission_changed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    fun dispatch(player: Player, regex: String) {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = regex
            )
        )
    }
}
