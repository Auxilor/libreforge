package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.get
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity

abstract class AttributeEffect(
    id: String,
    private val attribute: Attribute,
    private val operation: AttributeModifier.Operation
) : Effect<NoCompileData>(id) {
    protected abstract fun getValue(config: Config, entity: LivingEntity): Double

    private fun AttributeInstance.clean(name: String) {
        for (modifier in this.modifiers.toList()) {
            if (modifier.name == id || modifier.name == name) {
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
        val instance = entity.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"

        instance.clean(modifierName)

        instance.addModifier(
            AttributeModifier(
                identifiers.uuid,
                modifierName,
                getValue(config, entity),
                operation
            )
        )
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val entity = dispatcher.get<LivingEntity>() ?: return
        val instance = entity.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"

        instance.clean(modifierName)

        // Run on next tick to prevent constraining to the lower value during reloads.
        plugin.scheduler.run {
            constrainAttribute(entity, instance.value)
        }
    }
}
