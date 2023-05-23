package com.willfp.libreforge

import com.willfp.eco.core.data.get
import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.data.set
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class ItemPointsMap(
    private val pdc: PersistentDataContainer,
    private val parent: PersistentDataContainer
) {
    operator fun get(type: String): Double {
        return pdc.get(type, PersistentDataType.DOUBLE) ?: 0.0
    }

    operator fun set(type: String, value: Double) {
        pdc.set(type, PersistentDataType.DOUBLE, value)
        parent.set(key, PersistentDataType.TAG_CONTAINER, pdc)
    }
}

private val key = namespacedKeyOf("libreforge", "item_points")

val ItemStack.points: ItemPointsMap
    get() = this.fast().points

private val FastItemStack.pointsPDC: PersistentDataContainer
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(key, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(key, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }
        return pdc.get(key, PersistentDataType.TAG_CONTAINER)!!
    }

val FastItemStack.points: ItemPointsMap
    get() = ItemPointsMap(this.pointsPDC, this.persistentDataContainer)
