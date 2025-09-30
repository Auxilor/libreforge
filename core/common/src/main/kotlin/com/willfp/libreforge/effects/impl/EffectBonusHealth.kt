package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.templates.AttributeEffect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object EffectBonusHealth : AttributeEffect(
    "bonus_health",
    Attribute.GENERIC_MAX_HEALTH,
    AttributeModifier.Operation.ADD_NUMBER
) {
    override val arguments = arguments {
        require("health", "You must specify the bonus health to give!")
    }

    override val shouldReload = true

    override fun getValue(config: Config, entity: LivingEntity) =
        config.getDoubleFromExpression("health", entity as? Player)

    override fun constrainAttribute(entity: LivingEntity, value: Double) {
        if (entity.health > value) {
            entity.health = value
        }
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val bonusHealth = config.getDoubleFromExpression("amount", data);
        player.health = (player.health + bonusHealth)
            .coerceAtMost(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value)

        return true
    }
}
