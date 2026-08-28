package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerMeleeAttack
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent

object FilterFullyCharged : Filter<NoCompileData, Boolean>("fully_charged") {
    override val description = "Matches when the attack or bow shot is (or is not) fully charged."
    override val categories = setOf("combat")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when the event is not an attack or bow shot event.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        return when (val event = data.event) {
            is TriggerMeleeAttack.EcoEntityDamageByEntityEvent -> {
                val player = event.damager as? Player ?: return true
                Bukkit.getConsoleSender().sendMessage("Attack cooldown: ${player.attackCooldown}/${event.attackCooldown}")
                event.attackCooldown >= 1f == value
            }
            is EntityShootBowEvent -> {
                Bukkit.getConsoleSender().sendMessage("Force: ${event.force}")
                event.force >= 1f == value
            }
            else -> true
        }
    }
}
