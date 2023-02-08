package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

abstract class AttributeEffect(
    id: String,
    private val attribute: Attribute,
    private val operation: AttributeModifier.Operation
) : Effect<NoCompileData>(id) {
    protected abstract fun getValue(config: Config, player: Player): Double

    final override fun onEnable(player: Player, config: Config, identifiers: Identifiers, compileData: NoCompileData) {
        val instance = player.getAttribute(attribute) ?: return
        val uuid = identifiers.uuid
        instance.removeModifier(AttributeModifier(uuid, this.id, 0.0, operation))
        instance.addModifier(
            AttributeModifier(
                uuid,
                this.id,
                getValue(config, player),
                operation
            )
        )
    }

    final override fun onDisable(player: Player, identifiers: Identifiers) {
        val instance = player.getAttribute(attribute) ?: return
        instance.removeModifier(
            AttributeModifier(
                identifiers.uuid,
                this.id,
                0.0,
                operation
            )
        )
    }
}
