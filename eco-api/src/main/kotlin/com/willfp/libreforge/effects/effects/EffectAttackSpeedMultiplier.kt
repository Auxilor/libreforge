package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.JSONConfig
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player

class EffectAttackSpeedMultiplier : Effect("attack_speed_multiplier") {
    override fun handleEnable(player: Player, config: JSONConfig) {
        val attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED) ?: return
        val uuid = this.getUUID(player.getEffectAmount(this))
        attribute.removeModifier(AttributeModifier(uuid, this.id, 0.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1))
        attribute.addModifier(
            AttributeModifier(
                uuid,
                this.id,
                config.getDouble("multiplier") - 1,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )
    }

    override fun handleDisable(player: Player) {
        val attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED) ?: return
        attribute.removeModifier(
            AttributeModifier(
                this.getUUID(player.getEffectAmount(this)),
                this.id,
                0.0,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )
    }

    override fun validateConfig(config: JSONConfig): List<com.willfp.libreforge.ConfigViolation> {
        val violations = mutableListOf<com.willfp.libreforge.ConfigViolation>()

        config.getDoubleOrNull("multiplier")
            ?: violations.add(
                com.willfp.libreforge.ConfigViolation(
                    "multiplier",
                    "You must specify the attack speed multiplier!"
                )
            )

        return violations
    }
}