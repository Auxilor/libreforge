package com.willfp.libreforge.effects

import com.willfp.libreforge.LibReforgePlugin
import org.bukkit.NamespacedKey
import java.util.*

class IDGenerator(
    private val uuid: UUID
) {
    fun makeIdentifiers(offset: Int): Identifiers {
        return Identifiers(
            makeUUID(offset),
            makeKey(offset)
        )
    }

    private fun makeUUID(offset: Int): UUID {
        return UUID.nameUUIDFromBytes(
            "$uuid$offset".toByteArray()
        )
    }

    private fun makeKey(offset: Int): NamespacedKey {
        return LibReforgePlugin.instance.namespacedKeyFactory.create(
            "${uuid.hashCode()}_$offset"
        )
    }
}

data class Identifiers(
    val uuid: UUID,
    val key: NamespacedKey
)
