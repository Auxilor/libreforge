package com.willfp.libreforge

import com.willfp.eco.core.data.get
import com.willfp.eco.core.data.newPersistentDataContainer
import com.willfp.eco.core.data.remove
import com.willfp.eco.core.data.set
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.namespacedKeyOf
import jdk.javadoc.internal.doclets.toolkit.util.DocPath.parent
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

abstract class GenericItemDataMap<T : Any>(
    private val dataType: PersistentDataType<T, T>,
    private val rootKey: NamespacedKey,
) {
    protected abstract val pdc: PersistentDataContainer
    protected abstract val parent: PersistentDataContainer

    open operator fun get(key: String): T? {
        return pdc.get(key, dataType)
    }

    operator fun set(key: String, value: T) {
        pdc.set(key, dataType, value)
        parent.set(rootKey, PersistentDataType.TAG_CONTAINER, pdc)
    }

    operator fun minusAssign(type: String) = remove(type)

    fun remove(key: String) {
        pdc.remove(key)
        parent.set(rootKey, PersistentDataType.TAG_CONTAINER, pdc)
    }
}

class ItemDataMap(
    override val pdc: PersistentDataContainer,
    override val parent: PersistentDataContainer
) : GenericItemDataMap<String>(
    PersistentDataType.STRING,
    itemDataRootKey
)

private val itemDataRootKey = namespacedKeyOf("libreforge", "item_data")

val ItemStack.itemData: ItemDataMap
    get() = this.fast().itemData

private val FastItemStack.itemDataPDC: PersistentDataContainer
    get() {
        val pdc = this.persistentDataContainer
        if (!pdc.has(itemDataRootKey, PersistentDataType.TAG_CONTAINER)) {
            pdc.set(itemDataRootKey, PersistentDataType.TAG_CONTAINER, newPersistentDataContainer())
        }
        return pdc.get(itemDataRootKey, PersistentDataType.TAG_CONTAINER)!!
    }

val FastItemStack.itemData: ItemDataMap
    get() = ItemDataMap(this.itemDataPDC, this.persistentDataContainer)
