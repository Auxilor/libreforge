package com.willfp.libreforge.proxy.legacy

import com.willfp.libreforge.OpenInventoryAccessor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.CraftItemEvent

class OpenInventoryAccessorImpl: OpenInventoryAccessor {
    override fun getTopInventory(event: CraftItemEvent) =
        event.view.topInventory

    override fun getBottomInventory(event: CraftItemEvent) =
        event.view.bottomInventory

    override fun getTopInventory(player: Player) =
        player.openInventory.topInventory
}
