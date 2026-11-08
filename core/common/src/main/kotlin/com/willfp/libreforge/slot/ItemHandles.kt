package com.willfp.libreforge.slot

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

/**
 * The server stack behind an item mirror. Mirrors are new objects on every read, so two reads of
 * one slot only show the same stack through its handle.
 */
internal object ItemHandles {
    private val handleField: Field? = runCatching {
        Class.forName("${Bukkit.getServer().javaClass.packageName}.inventory.CraftItemStack")
            .getDeclaredField("handle")
            .apply { isAccessible = true }
    }.getOrNull()

    /**
     * The stack [item] mirrors, or [item] itself when it mirrors nothing.
     */
    fun handleOf(item: ItemStack): Any =
        handleField?.takeIf { it.declaringClass.isInstance(item) }?.get(item) ?: item
}
