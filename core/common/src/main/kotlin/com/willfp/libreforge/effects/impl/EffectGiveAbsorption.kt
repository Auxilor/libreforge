package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EquipmentSlotGroup

object EffectGiveAbsorption : Effect<NoCompileData>("give_absorption") {
    override val description = "Grants the player additional absorption hearts, expanding the max absorption cap until the absorption runs out."
    override val categories = setOf("player", "attribute")

    private val modifierKey by lazy { plugin.createNamespacedKey("give_absorption") }

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "amount",
            "You must specify the amount of absorption hearts!",
            description = "The number of absorption hearts to add. Supports expressions.",
            type = ArgType.EXPRESSION,
            example = "%level%"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val newAmount = player.absorptionAmount + config.getDoubleFromExpression("amount", data)
        player.getAttribute(Attribute.MAX_ABSORPTION)?.let { attribute ->
            if (attribute.baseValue > attribute.defaultValue) {
                attribute.baseValue = attribute.defaultValue
            }

            attribute.removeExpansion()

            if (newAmount > attribute.value) {
                attribute.addModifier(
                    AttributeModifier(
                        modifierKey,
                        newAmount - attribute.value,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.ANY
                    )
                )
            }
        }
        player.absorptionAmount = newAmount
        return true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        val attribute = player.getAttribute(Attribute.MAX_ABSORPTION)
            ?.takeIf { it.getModifier(modifierKey) != null }
            ?: return

        plugin.scheduler.on(player).run {
            if (player.absorptionAmount <= 0.0) {
                attribute.removeExpansion()
            }
        }
    }

    private fun AttributeInstance.removeExpansion() {
        this.getModifier(modifierKey)?.let { this.removeModifier(it) }
    }
}
