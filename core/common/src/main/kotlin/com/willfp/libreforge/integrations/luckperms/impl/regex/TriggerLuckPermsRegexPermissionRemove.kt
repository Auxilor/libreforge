package com.willfp.libreforge.integrations.luckperms.impl.regex

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.RegexPermissionNode
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLuckPermsRegexPermissionRemove : Trigger("regex_permission_removed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NodeRemoveEvent) {
        if (!event.isUser) return
        val player = Bukkit.getPlayer((event.target as User).uniqueId) ?: return
        val node = event.node as? RegexPermissionNode ?: return

        TriggerLuckPermsRegexPermissionChange.dispatch(player, node.patternString)

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = node.patternString
            )
        )
    }
}
