package com.willfp.libreforge.display

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.display.DisplayModule
import com.willfp.eco.core.display.DisplayPriority
import com.willfp.eco.core.fast.fast
import com.willfp.libreforge.LibreforgeSpigotPlugin
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class AttributeHider(
    private val plugin: LibreforgeSpigotPlugin
) : DisplayModule(plugin, DisplayPriority.HIGHEST) {
    private val flags = mutableSetOf(ItemFlag.HIDE_ATTRIBUTES)

    private val pdcKey = plugin.createNamespacedKey("display_flags")

    init {
        if (Prerequisite.HAS_1_20_5.isMet) {
            val hideAdditionalTooltip = ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP")
            flags.add(hideAdditionalTooltip)
        }
    }

    override fun display(itemStack: ItemStack, vararg args: Any) {
        if (!plugin.configYml.getBool("display.hide-attributes")) {
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
        if (!plugin.configYml.getBool("display.hide-attributes")) {
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
