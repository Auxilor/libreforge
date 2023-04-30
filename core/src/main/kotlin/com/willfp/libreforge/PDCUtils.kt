package com.willfp.libreforge

import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.annotations.Contract
import java.util.UUID

val PersistentDataHolder.pdc get() = this.persistentDataContainer
fun PersistentDataContainer.setBool(key: NamespacedKey, value: Boolean) {
    this.set(key, PersistentDataType.BYTE, if (value) 1 else 0)
}
fun PersistentDataContainer.getBool(key: NamespacedKey, default: Boolean = false) : Boolean {
    val value = get(key, PersistentDataType.BYTE) ?: return default
    return value == 1.toByte()
}
fun PersistentDataContainer.hasBool(key: NamespacedKey): Boolean {
    return has(key, PersistentDataType.BYTE)
}
fun PersistentDataContainer.setUUID(key: NamespacedKey, uuid: UUID) {
    this.set(key, PersistentDataType.LONG_ARRAY, longArrayOf(uuid.leastSignificantBits, uuid.mostSignificantBits))
}
fun PersistentDataContainer.getUUID(key: NamespacedKey) : UUID? {
    val arr = get(key, PersistentDataType.LONG_ARRAY) ?: return null
    if (arr.size != 2) return null
    return UUID(arr[1], arr[0])
}
fun PersistentDataContainer.hasUUID(key: NamespacedKey) : Boolean {
    return has(key, PersistentDataType.LONG_ARRAY)
}
fun PersistentDataContainer.setDouble(key: NamespacedKey, double: Double) {
    this.set(key, PersistentDataType.DOUBLE, double)
}
fun PersistentDataContainer.getDouble(key: NamespacedKey) : Double? {
    return get(key, PersistentDataType.DOUBLE)
}
fun PersistentDataContainer.hasDouble(key: NamespacedKey) : Boolean {
    return has(key, PersistentDataType.DOUBLE)
}
fun PersistentDataContainer.setFloat(key: NamespacedKey, float: Float) {
    this.set(key, PersistentDataType.FLOAT, float)
}
fun PersistentDataContainer.getFloat(key: NamespacedKey) : Float? {
    return get(key, PersistentDataType.FLOAT)
}
fun PersistentDataContainer.hasFloat(key: NamespacedKey) : Boolean {
    return has(key, PersistentDataType.FLOAT)
}
fun PersistentDataContainer.setInt(key: NamespacedKey, int: Int) {
    this.set(key, PersistentDataType.INTEGER, int)
}
fun PersistentDataContainer.getInt(key: NamespacedKey) : Int? {
    return get(key, PersistentDataType.INTEGER)
}
fun PersistentDataContainer.hasInt(key: NamespacedKey) : Boolean {
    return has(key, PersistentDataType.INTEGER)
}
fun PersistentDataContainer.setString(key: NamespacedKey, string: String) {
    this.set(key, PersistentDataType.STRING, string)
}
fun PersistentDataContainer.getString(key: NamespacedKey) : String? {
    return get(key, PersistentDataType.STRING)
}
fun PersistentDataContainer.hasString(key: NamespacedKey) : Boolean {
    return has(key, PersistentDataType.STRING)
}

@Contract("_,true->!null")
fun PersistentDataContainer.getPDC(key: NamespacedKey, save: Boolean = true): PersistentDataContainer? {
    var pdc = get(key, PersistentDataType.TAG_CONTAINER)
    if (pdc == null && save) {
        pdc = this.adapterContext.newPersistentDataContainer()
        set(key, PersistentDataType.TAG_CONTAINER, pdc)
    }
    return pdc
}

val Block.pdc : PersistentDataContainer get() {
    return this.chunk.pdc.getPDC(NamespacedKey(plugin, "block:$x;$y;$z"))!!
}
fun Block.getPDCNoSave(): PersistentDataContainer? {
    return this.chunk.pdc.getPDC(NamespacedKey(plugin, "block:$x;$y;$z"), false)
}