package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


object EffectDamageItem : Effect<NoCompileData>("damage_item") {
    override val isPermanent = false

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val item = data.holder.getProvider<ItemStack>() ?: data.item ?: return false
        val victim = data.victim

        val damage = config.getIntFromExpression("damage", data)

        val meta = item.itemMeta ?: return false

        if (meta.isUnbreakable || meta !is Damageable) {
            return false
        }

        // Edge cases
        if (item.type == Material.CARVED_PUMPKIN || item.type == Material.PLAYER_HEAD) {
            return false
        }

        if (victim is Player) {
            val event = PlayerItemDamageEvent(victim, item, damage)
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) {
                applyDamage(item, event.damage, victim)
            }
        } else {
            applyDamage(item, damage, null)
        }

        return true
    }

    private fun applyDamage(itemStack: ItemStack, amount: Int, player: Player?) {
        val meta = itemStack.itemMeta as? Damageable ?: return

        meta.damage += amount

        if (meta.damage >= itemStack.type.maxDurability) {
            meta.damage = itemStack.type.maxDurability.toInt()

            itemStack.itemMeta = meta

            if (player != null) {
                Bukkit.getPluginManager().callEvent(PlayerItemBreakEvent(player, itemStack))
                player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1f, 1f)
            }

            itemStack.type = Material.AIR
        } else {
            itemStack.itemMeta = meta
        }
    }
}
