package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


object EffectDamageMainhand : Effect<NoCompileData>("damage_mainhand") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val damage = config.getIntFromExpression("damage", data)

        val equipment = victim.equipment ?: return false

        @Suppress("USELESS_ELVIS") // I've had it be null before, spigot sucks.
        val item = equipment.itemInMainHand ?: return false

        val meta = item.itemMeta as? Damageable ?: return false

        if (meta.isUnbreakable) {
            return false
        }

        return if (victim is Player) {
            val event = PlayerItemDamageEvent(victim, item, damage)
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) {
                applyDamage(item, event.damage, victim)
            } else {
                false
            }
        } else {
            applyDamage(item, damage, null)
        }
    }

    private fun applyDamage(itemStack: ItemStack, amount: Int, player: Player?): Boolean {
        val meta = itemStack.itemMeta as? Damageable ?: return false
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

        return true
    }
}
