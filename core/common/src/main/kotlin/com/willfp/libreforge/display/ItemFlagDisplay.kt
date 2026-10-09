package com.willfp.libreforge.display

import com.willfp.eco.core.display.DisplayContext
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
    @Volatile
    private var flags = emptySet<ItemFlag>()

    @Volatile
    private var enabled = false

    private val pdcKey = plugin.createNamespacedKey("display_flags")

    init {
        reload()
    }

    internal fun reload() {
        enabled = plugin.configYml.getBool("display.enabled")

        val newFlags = mutableSetOf<ItemFlag>()

        for (flagName in plugin.configYml.getStrings("display.item-flags")) {
            try {
                newFlags += ItemFlag.valueOf(flagName.uppercase())
            } catch (e: IllegalArgumentException) {
                plugin.logger.warning("Invalid item flag for display.item-flags: $flagName")
                plugin.logger.warning("Valid options are: ${ItemFlag.entries.joinToString(", ") { it.name.lowercase() }}")
            }
        }

        flags = newFlags
    }

    override fun display(context: DisplayContext) {
        if (!enabled) {
            return
        }

        val flags = flags
        val fis = context.itemStack.fast()

        fis.persistentDataContainer.set(pdcKey, PersistentDataType.STRING, flags.joinToString(","))
        fis.addItemFlags(*flags.toTypedArray())
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun revert(itemStack: ItemStack) {
        if (!enabled) {
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
