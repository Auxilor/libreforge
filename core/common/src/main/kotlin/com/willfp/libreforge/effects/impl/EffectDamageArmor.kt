package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable


object EffectDamageArmor : Effect<NoCompileData>("damage_armor") {
    override val parameters = setOf(
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        require("damage", "You must specify the amount of damage!")
    }

    @Suppress("DEPRECATION")
    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val victim = data.victim ?: return false

        val damage = config.getIntFromExpression("damage", data)

        val equipment = victim.equipment ?: return false

        val slots = config.getStrings("slots").mapNotNull {
            enumValueOfOrNull<EquipmentSlot>(it.uppercase())
        }

        if (slots.isEmpty()) {
            for (item in equipment.armorContents) {
                @Suppress("SENSELESS_COMPARISON")
                if (item == null) {
                    continue
                }

                val meta = item.itemMeta ?: continue

                if (meta.isUnbreakable) {
                    continue
                }

                if (meta !is Damageable) {
                    continue
                }

                // edge cases
                if (arrayOf(Material.CARVED_PUMPKIN, Material.PLAYER_HEAD).contains(item.type)) {
                    continue
                }

                if (victim is Player) {
                    @Suppress("DEPRECATION")
                    val event = PlayerItemDamageEvent(victim, item, damage)
                    Bukkit.getPluginManager().callEvent(event)
                    if (!event.isCancelled) {
                        item.applyDamage(event.damage, victim)
                    }
                } else {
                    item.applyDamage(damage, null)
                }
            }
        } else {
            for (slot in slots) {
                val item = equipment.getItem(slot)
                @Suppress("SENSELESS_COMPARISON")
                if (item != null && item.itemMeta is Damageable) {
                    if (victim is Player) {
                        @Suppress("DEPRECATION")
                        val event = PlayerItemDamageEvent(victim, item, damage)
                        Bukkit.getPluginManager().callEvent(event)
                        if (!event.isCancelled) {
                            item.applyDamage(event.damage, victim)
                        }
                    } else {
                        item.applyDamage(damage, null)
                    }
                }
            }
        }

        return true
    }
}