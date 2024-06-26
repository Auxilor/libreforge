package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlotGroup
import java.util.UUID

abstract class AttributeEffect(
    id: String,
    private val attribute: Attribute,
    private val operation: AttributeModifier.Operation
) : Effect<NoCompileData>(id) {
    protected abstract fun getValue(config: Config, entity: LivingEntity): Double

    protected open fun canApplyTo(entity: LivingEntity): Boolean = true

    private fun AttributeInstance.clean(name: String, identifiers: Identifiers) {
        for (modifier in this.modifiers.toList()) {
            if (modifier.name == id || modifier.name == name || modifier.name == identifiers.key.key) {
                this.removeModifier(modifier)
            }
        }
    }

    open fun constrainAttribute(entity: LivingEntity, value: Double) {
        // Override this to constrain the attribute value, e.g. to set health below max health.
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val entity = dispatcher.get<LivingEntity>() ?: return

        if (!canApplyTo(entity)) {
            return
        }

        val instance = entity.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"

        instance.clean(modifierName, identifiers)

        val modifier = attributeModifier(
            identifiers,
            modifierName,
            getValue(config, entity),
            operation
        )

        // Extra check to prevent adding the same modifier twice.
        instance.removeModifier(modifier)
        instance.addModifier(modifier)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val entity = dispatcher.get<LivingEntity>() ?: return

        if (!canApplyTo(entity)) {
            return
        }

        val instance = entity.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"

        instance.clean(modifierName, identifiers)

        instance.removeModifier(
            attributeModifier(
                identifiers,
                modifierName,
                0.0,
                operation
            )
        )

        // Run on next tick to prevent constraining to the lower value during reloads.
        plugin.scheduler.run {
            constrainAttribute(entity, instance.value)
        }
    }

    private fun attributeModifier(
        identifiers: Identifiers,
        name: String,
        value: Double,
        operation: AttributeModifier.Operation
    ): AttributeModifier {
        return if (Prerequisite.HAS_1_21.isMet) {
            @Suppress("UnstableApiUsage")
            AttributeModifier(
                identifiers.key,
                value,
                operation,
                EquipmentSlotGroup.ANY
            )
        } else {
            @Suppress("DEPRECATION", "REMOVAL")
            AttributeModifier(
                UUID.randomUUID(),
                name,
                value,
                operation
            )
        }
    }
}
