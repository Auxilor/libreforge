package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.integrations.mythicmobs.utils.isMythicMob
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.tryAsLivingEntity
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

object TriggerTakeDamage : Trigger("take_damage") {
    var blockEntityDamageByEntity: Boolean = false

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.VALUE
    )

    private val ignoredCauses = setOf(
        EntityDamageEvent.DamageCause.VOID,
        EntityDamageEvent.DamageCause.SUICIDE,
        EntityDamageEvent.DamageCause.KILL
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: EntityDamageEvent) {
        if (event.cause in ignoredCauses) return
        // If Damager (or Projectile Shooter) is MythicMob, then skip. Use 'take_mythic_damage' instead.
        if (event is EntityDamageByEntityEvent && event.damager.tryAsLivingEntity()?.isMythicMob() == true) return
        if (blockEntityDamageByEntity && event is EntityDamageByEntityEvent) return

        val victim = event.entity
        this.dispatch(
            victim.toDispatcher(),
            TriggerData(
                player = victim as? Player,
                victim = victim as? LivingEntity,
                event = event,
                value = event.finalDamage
            )
        )
    }

    fun notifyOfEntityDamageChange(sender: CommandSender? = null) {
        // TODO remove on 5th May 2026.
        val optedIn = plugin.configYml.getBool("opt-in.take_damage_blocks_entity_damage")
        val blockEntityDamageByEntityCutOff = LocalDate.of(2026, Month.MAY, 4)
        val today = LocalDate.now()
        val shouldDisableEntityDamageByEntity = today.isAfter(blockEntityDamageByEntityCutOff) || today.isEqual(blockEntityDamageByEntityCutOff) || optedIn
        if (!optedIn) {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val dateString = blockEntityDamageByEntityCutOff.format(formatter)
            val messages = listOf(
                "On $dateString 'take_damage' will no longer trigger for Entity damage.",
                "Please migrate all relevant usages of 'take_damage' to 'take_entity_damage' before this date.",
                "Alternatively, enable 'opt-in.take_damage_blocks_entity_damage' to apply the internal change before $dateString."
            )
            messages.forEach { message ->
                sender?.sendMessage("§c$message")
                plugin.logger.warning(message)
            }
        }
        blockEntityDamageByEntity = shouldDisableEntityDamageByEntity
    }
}