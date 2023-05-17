package com.willfp.libreforge.entities

import com.willfp.eco.core.entities.CustomEntity
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity

private val goTrue: (Entity) -> Boolean = { true }
private val goNull: (Location) -> Entity? = { null }

abstract class LibreforgeCustomEntity(key: NamespacedKey) : CustomEntity(key, goTrue, goNull) {
    abstract override fun matches(entity: Entity?): Boolean

    abstract override fun spawn(location: Location): Entity

    override fun register() {
        throw UnsupportedOperationException("LibreforgeCustomEntity cannot be registered")
    }
}
