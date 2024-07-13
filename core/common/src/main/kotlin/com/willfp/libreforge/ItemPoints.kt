package com.willfp.libreforge

import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class ItemPointsMap(
    override val pdc: PersistentDataContainer,
    override val parent: PersistentDataContainer
): GenericItemDataMap<Double>(
    PersistentDataType.DOUBLE,
    itemPointsRootKey
) {
    override operator fun get(key: String): Double =
        super.get(key) ?: 0.0
}

private val itemPointsRootKey = namespacedKeyOf("libreforge", "item_points")

val ItemStack.points: ItemPointsMap
    get() = this.fast().points

private val FastItemStack.pointsPDC: PersistentDataContainer
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(itemPointsRootKey, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(itemPointsRootKey, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }
        return pdc.get(itemPointsRootKey, PersistentDataType.TAG_CONTAINER)!!
    }

val FastItemStack.points: ItemPointsMap
    get() = ItemPointsMap(this.pointsPDC, this.persistentDataContainer)
