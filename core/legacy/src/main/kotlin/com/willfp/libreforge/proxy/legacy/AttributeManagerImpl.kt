package com.willfp.libreforge.proxy.legacy

import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier

class AttributeManagerImpl : AttributeEffect.AttributeManager {
    override fun createModifier(
        identifiers: Identifiers,
        name: String,
        value: Double,
        operation: AttributeModifier.Operation
    ) = AttributeModifier(
        identifiers.uuid,
        name,
        value,
        operation
    )
}
