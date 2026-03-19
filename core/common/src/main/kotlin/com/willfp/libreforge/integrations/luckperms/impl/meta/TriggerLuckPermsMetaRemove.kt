package com.willfp.libreforge.integrations.luckperms.impl.meta

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import net.luckperms.api.node.types.MetaNode
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

object TriggerLuckPermsMetaRemove : Trigger("meta_removed") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.TEXT
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: NodeRemoveEvent) {
        if (!event.isUser) return
        val player = Bukkit.getPlayer((event.target as User).uniqueId) ?: return
        val node = event.node as? MetaNode ?: return

        TriggerLuckPermsMetaChange.dispatch(player, node.metaKey, node.metaValue)

        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                text = "${node.metaKey}:${node.metaValue}"
            )
        )
    }
}
