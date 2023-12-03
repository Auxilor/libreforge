package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object EffectDropSlotItem : Effect<NoCompileData>("drop_slot_item") {
    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require("slot", "You must specify the slot to drop the item from!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val slot = config.getIntFromExpression("slot", player)

        val amount = config.getIntOrNull("amount")

        val item = player.inventory.getItem(slot) ?: return false

        if (item.type.isAir) return false

        if (amount != null && item.amount < amount) return false

        val toLeave = if (amount != null) {
            (item.amount - amount).coerceAtLeast(0)
        } else 0

        val itemToSet = if (toLeave <= 0) null else item.clone().apply { this.amount = toLeave }

        val itemToDrop = item.clone().apply { this.amount = (amount ?: this.amount) }

        val drop = player.location.world.dropItem(player.eyeLocation, itemToDrop)

        drop.velocity = player.eyeLocation.direction.clone().multiply(0.3)

        player.inventory.setItem(slot, itemToSet)

        player.updateInventory()

        return true
    }
}
