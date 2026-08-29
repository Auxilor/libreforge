package com.willfp.libreforge.triggers.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import com.google.common.base.Function
import org.bukkit.Bukkit
import org.bukkit.damage.DamageSource
import org.bukkit.entity.Entity
import org.bukkit.event.Listener

object TriggerMeleeAttack : Trigger("melee_attack") {
    private val dataMap = ConcurrentHashMap<UUID, Float>()

    override val description = "Fires when the player lands a melee hit on an entity."

    override val categories = setOf("combat")

    override val parameterDescriptions = mapOf(
        TriggerParameter.VICTIM to "The entity that was hit.",
        TriggerParameter.LOCATION to "The victim's location at the time of the hit.",
        TriggerParameter.ITEM to "The item in the attacker's main hand.",
        TriggerParameter.VALUE to "The damage dealt.",
        TriggerParameter.ALT_VALUE to "The attack's base damage, before armour, resistance, or other modifiers."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM,
        TriggerParameter.EVENT,
        TriggerParameter.LOCATION,
        TriggerParameter.ITEM,
        TriggerParameter.VALUE,
        TriggerParameter.ALT_VALUE
    )

    private val processedEvents = mutableSetOf<UUID>()

    fun registerPaperExclusiveListeners() {
        val mcVersion = Bukkit.getServer().bukkitVersion.split("-").getOrNull(0)?.split(".")?.getOrNull(0)?.toInt() ?: 0
        if (mcVersion >= 26) {
            plugin.logger.info("Registering PrePlayerAttackEntityEvent for Minecraft version $mcVersion")
            plugin.eventManager.registerListener(PaperExclusiveListeners())
        }
    }

    private class PaperExclusiveListeners : Listener {
        @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun handle(event: PrePlayerAttackEntityEvent) {
            if (!(event.willAttack())) return
            dataMap[event.player.uniqueId] = event.player.attackCooldown
            plugin.scheduler.runLater({
                dataMap.remove(event.player.uniqueId)
            }, 1L)
        }
    }

    class EcoEntityDamageByEntityEvent(
        damager: Entity,
        damagee: Entity,
        cause: DamageCause,
        damage: Double,
        val attackCooldown: Float,
    ) : EntityDamageByEntityEvent(damager, damagee, cause, damage) {
        constructor(event: EntityDamageByEntityEvent, attackCooldown: Float) : this(
            damager = event.damager,
            damagee = event.entity,
            cause = event.cause,
            damage = event.damage,
            attackCooldown = attackCooldown
        )
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun handle(event: EntityDamageByEntityEvent) {
        val attacker = event.damager as? LivingEntity ?: return
        val victim = event.entity as? LivingEntity ?: return

        if (event.cause == EntityDamageEvent.DamageCause.THORNS) {
            return
        }

        if (processedEvents.contains(event.entity.uniqueId)) {
            return
        }

        processedEvents.add(event.entity.uniqueId)

        this.dispatch(
            attacker.toDispatcher(),
            TriggerData(
                player = attacker as? Player,
                victim = victim,
                location = victim.location,
                event = EcoEntityDamageByEntityEvent(
                    event = event,
                    attackCooldown = dataMap[event.damager.uniqueId] ?: (attacker as? Player)?.attackCooldown ?: 0f
                ),
                item = attacker.equipment?.itemInMainHand,
                value = event.finalDamage,
                altValue = event.getDamage(EntityDamageEvent.DamageModifier.BASE)
            )
        )

        processedEvents.clear()
    }
}
