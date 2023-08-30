package com.willfp.libreforge

import com.willfp.eco.core.data.get
import com.willfp.eco.core.data.has
import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.data.remove
import com.willfp.eco.core.data.set
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class ItemDataMap(
    private val pdc: PersistentDataContainer,
    private val parent: PersistentDataContainer
) {
    operator fun get(type: String): String? {
        return pdc.get(type, PersistentDataType.STRING)
    }

    operator fun set(type: String, value: String) {
        pdc.set(type, PersistentDataType.STRING, value)
        parent.set(key, PersistentDataType.TAG_CONTAINER, pdc)
    }

    operator fun minusAssign(type: String) = remove(type)

    fun remove(type: String) {
        pdc.remove(type)
        parent.set(key, PersistentDataType.TAG_CONTAINER, pdc)
    }
}

private val key = namespacedKeyOf("libreforge", "item_data")

val ItemStack.itemData: ItemDataMap
    get() = this.fast().itemData

private val FastItemStack.itemDataPDC: PersistentDataContainer
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(key, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(key, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }
        return pdc.get(key, PersistentDataType.TAG_CONTAINER)!!
    }

val FastItemStack.itemData: ItemDataMap
    get() = ItemDataMap(this.itemDataPDC, this.persistentDataContainer)
