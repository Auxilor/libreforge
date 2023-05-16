package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

abstract class AttributeEffect(
    id: String,
    private val attribute: Attribute,
    private val operation: AttributeModifier.Operation
) : Effect<NoCompileData>(id) {
    protected abstract fun getValue(config: Config, player: Player): Double

    private fun AttributeInstance.clean(name: String) {
        for (modifier in this.modifiers.toList()) {
            if (modifier.name == id || modifier.name == name) {
                this.removeModifier(modifier)
            }
        }
    }

    open fun constrainAttribute(player: Player, value: Double) {
        // Override this to constrain the attribute value, e.g. to set health below max health.
    }

    final override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val instance = player.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"

        instance.clean(modifierName)

        instance.addModifier(
            AttributeModifier(
                identifiers.uuid,
                modifierName,
                getValue(config, player),
                operation
            )
        )
    }

    final override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        val instance = player.getAttribute(attribute) ?: return
        val modifierName = "libreforge:${this.id} - ${identifiers.key.key} (${holder.holder.id})"
        instance.clean(modifierName)

        instance.removeModifier(
            AttributeModifier(
                identifiers.uuid,
                modifierName,
                0.0,
                operation
            )
        )

        // Run on next tick to prevent constraining to the lower value during reloads.
        plugin.scheduler.run {
            constrainAttribute(player, instance.value)
        }
    }
}
