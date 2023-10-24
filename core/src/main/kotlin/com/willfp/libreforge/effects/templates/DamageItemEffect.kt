package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.applyDamage
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


abstract class DamageItemEffect(id: String) : Effect<NoCompileData>(id) {
    final override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    abstract fun getItems(data: TriggerData): List<ItemStack>

    final override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val damage = config.getIntFromExpression("damage", data)

        val items = getItems(data)
            .filterNot { it.type == Material.CARVED_PUMPKIN }
            .filterNot { it.type == Material.PLAYER_HEAD }

        var success = false

        for (item in items) {
            val meta = item.itemMeta as? Damageable ?: continue

            if (meta.isUnbreakable) {
                continue
            }

            if (victim is Player) {
                @Suppress("DEPRECATION")
                val event = PlayerItemDamageEvent(victim, item, damage)
                Bukkit.getPluginManager().callEvent(event)
                if (!event.isCancelled) {
                    item.applyDamage(event.damage, victim)
                    success = true
                }
            } else {
                item.applyDamage(damage, null)
                success = true
            }
        }

        return success
    }
}
