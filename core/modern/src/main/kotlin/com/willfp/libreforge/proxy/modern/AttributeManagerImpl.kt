package com.willfp.libreforge.proxy.modern

import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.templates.AttributeEffect
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup

class AttributeManagerImpl : AttributeEffect.AttributeManager {
    @Suppress("UnstableApiUsage")
    override fun createModifier(
        identifiers: Identifiers,
        name: String,
        value: Double,
        operation: AttributeModifier.Operation
    ) = AttributeModifier(
        identifiers.key,
        value,
        operation,
        EquipmentSlotGroup.ANY
    )
}
