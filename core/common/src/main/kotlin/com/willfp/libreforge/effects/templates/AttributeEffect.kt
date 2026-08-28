package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlotGroup

abstract class AttributeEffect private constructor(
    id: String,
    attributeProvider: () -> Attribute?,
    private val operation: AttributeModifier.Operation
) : Effect<NoCompileData>(id) {
    constructor(
        id: String,
        attribute: Attribute,
        operation: AttributeModifier.Operation
    ) : this(id, { attribute }, operation)

    /**
     * For attributes that don't exist on all supported server versions, looked up by key at
     * runtime. The effect silently does nothing if the attribute isn't present.
     */
    constructor(
        id: String,
        attributeKey: String,
        operation: AttributeModifier.Operation
    ) : this(id, { Registry.ATTRIBUTE.get(NamespacedKey.minecraft(attributeKey)) }, operation)

    private val attribute by lazy(attributeProvider)

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

        val attribute = this.attribute ?: return
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

        val attribute = this.attribute ?: return
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
    ) = AttributeModifier(
        identifiers.key,
        value,
        operation,
        EquipmentSlotGroup.ANY
    )
}
