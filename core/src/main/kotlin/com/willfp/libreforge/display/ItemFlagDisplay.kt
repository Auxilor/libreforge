package com.willfp.libreforge.display

import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.libreforge.LibreforgeSpigotPlugin
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemFlagDisplay(
    private val plugin: LibreforgeSpigotPlugin
) : DisplayModule(plugin, DisplayPriority.HIGHEST) {
    private val flags = mutableSetOf<ItemFlag>()

    private val pdcKey = plugin.createNamespacedKey("display_flags")

    init {
        reload()
    }

    internal fun reload() {
        flags.clear()

        for (flagName in plugin.configYml.getStrings("display.item-flags")) {
            try {
                flags += ItemFlag.valueOf(flagName.uppercase())
            } catch (e: IllegalArgumentException) {
                plugin.logger.warning("Invalid item flag for display.item-flags: $flagName")
                plugin.logger.warning("Valid options are: ${ItemFlag.entries.joinToString(", ") { it.name.lowercase() }}")
            }
        }
    }

    override fun display(itemStack: ItemStack, vararg args: Any) {
        if (flags.isEmpty()) {
            return
        }

        val fis = itemStack.fast()

        var existingFlags = ""

        for (flag in flags) {
            if (fis.hasItemFlag(flag)) {
                existingFlags += flag.toString()
            }
        }

        fis.persistentDataContainer.set(pdcKey, PersistentDataType.STRING, flags.joinToString(","))

        flags.forEach { fis.addItemFlags(it) }
    }

    override fun revert(itemStack: ItemStack) {
        if (flags.isEmpty()) {
            return
        }

        val fis = itemStack.fast()

        fis.removeItemFlags(*flags.toTypedArray())

        val existingFlags = fis.persistentDataContainer.get(pdcKey, PersistentDataType.STRING) ?: return

        fis.persistentDataContainer.remove(pdcKey)

        val serverFlags = existingFlags.split(",").map { ItemFlag.valueOf(it) }
        fis.addItemFlags(*serverFlags.toTypedArray())
    }
}
