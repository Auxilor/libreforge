package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.functional.SlotHandler
import com.willfp.eco.core.items.Items
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun MenuBuilder.addCustomSlots(configs: List<Config>) {
    for (customSlot in configs) {
        setSlot(
            customSlot.getInt("row"),
            customSlot.getInt("column"),
            slot(Items.lookup(customSlot.getString("item")).item) {
                fun dispatchCommands(key: String): SlotHandler =
                    SlotHandler { event, _, _ ->
                        val player = event.whoClicked as Player

                        for (string in customSlot.getStrings(key)) {
                            if (string.startsWith("console:")) {
                                Bukkit.getServer().dispatchCommand(
                                    Bukkit.getConsoleSender(),
                                    string.removePrefix("console:")
                                )
                            } else {
                                Bukkit.getServer().dispatchCommand(player, string)
                            }
                        }
                    }

                onLeftClick(dispatchCommands("left-click"))
                onLeftClick(dispatchCommands("right-click"))
                onShiftLeftClick(dispatchCommands("shift-left-click"))
                onShiftRightClick(dispatchCommands("shift-right-click"))
            }
        )
    }
}
