package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectBonusHealth : Effect("bonus_health") {
    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        val uuid = identifiers.uuid
        attribute.removeModifier(AttributeModifier(uuid, this.id, 0.0, AttributeModifier.Operation.ADD_NUMBER))

        attribute.addModifier(
            AttributeModifier(
                uuid,
                this.id,
                config.getIntFromExpression("health", player).toDouble(),
                AttributeModifier.Operation.ADD_NUMBER
            )
        )
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
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

        if (!config.has("health")) violations.add(
            ConfigViolation(
                "health",
                "You must specify the bonus health to give!"
            )
        )

        return violations
    }
}