package com.willfp.libreforge.effects

import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.NamespacedKey
import java.util.UUID

/**
 * Generates IDs around a base UUID.
 */
class IdentifierFactory(
    private val uuid: UUID
) {
    fun makeIdentifiers(offset: Int) = Identifiers(
        makeUUID(offset),
        makeKey(offset)
    )

    private fun makeUUID(offset: Int) =
        UUID.nameUUIDFromBytes("$uuid$offset".toByteArray())

    private fun makeKey(offset: Int) =
        NamespacedKeyUtils.createEcoKey("${uuid.hashCode()}_$offset")
}

data class Identifiers(
    val uuid: UUID,
    val key: NamespacedKey
) {
    /**
     * Make a spin-off factory.
     */
    fun makeFactory() = IdentifierFactory(uuid)
}
