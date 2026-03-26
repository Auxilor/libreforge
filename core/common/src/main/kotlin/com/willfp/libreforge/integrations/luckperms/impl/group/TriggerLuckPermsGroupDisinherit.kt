package com.willfp.libreforge.integrations.luckperms.impl.group

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLuckPermsGroupDisinherit : Trigger("group_disinherited") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NodeRemoveEvent) {
        if (!event.isUser) return
        val player = Bukkit.getPlayer((event.target as User).uniqueId) ?: return
        val node = event.node as? InheritanceNode ?: return

        TriggerLuckPermsGroupChange.dispatch(player, node.groupName)

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = node.groupName
            )
        )
    }
}
