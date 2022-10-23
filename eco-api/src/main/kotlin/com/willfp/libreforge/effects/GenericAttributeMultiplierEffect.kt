package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

abstract class GenericAttributeMultiplierEffect(
    id: String,
    private val attribute: Attribute,
    private val operation: AttributeModifier.Operation
) : Effect(id, shouldRefresh = true) {
    protected abstract fun getValue(config: Config, player: Player): Double

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
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

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
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
