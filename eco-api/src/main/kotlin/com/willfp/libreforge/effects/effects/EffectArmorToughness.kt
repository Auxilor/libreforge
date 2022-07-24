package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectArmorToughness : Effect("armor_toughness") {
    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val attribute = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) ?: return
        val uuid = identifiers.uuid
        attribute.removeModifier(AttributeModifier(uuid, this.id, 0.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1))
        attribute.addModifier(
            AttributeModifier(
                uuid,
                this.id,
                config.getIntFromExpression("points", player).toDouble(),
                AttributeModifier.Operation.ADD_NUMBER
            )
        )
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val attribute = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS) ?: return
        attribute.removeModifier(
            AttributeModifier(
                identifiers.uuid,
                this.id,
                0.0,
                AttributeModifier.Operation.ADD_NUMBER
            )
        )
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("points")) violations.add(
            ConfigViolation(
                "points",
                "You must specify the amount of points to add/remove!"
            )
        )

        return violations
    }
}