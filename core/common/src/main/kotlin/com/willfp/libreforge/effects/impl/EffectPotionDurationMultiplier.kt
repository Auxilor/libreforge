package com.willfp.libreforge.effects.impl

import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect

object EffectPotionDurationMultiplier : MultiplierEffect("potion_duration_multiplier") {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        val player = event.contents.viewers.filterIsInstance<Player>().firstOrNull() ?: return

        val multiplier = getMultiplier(player.toDispatcher())

        plugin.scheduler.run {
            for (i in 0..2) {
                val item = event.contents.getItem(i) ?: continue
                val meta = item.itemMeta as? PotionMeta ?: continue

                val potionType = meta.basePotionType ?: continue
                val effects = potionType.potionEffects

                // Skip potions with no effects (e.g., water, awkward, mundane, thick)
                if (effects.isEmpty()) {
                    continue
                }

                // Store multiplier for later application
                if (multiplier != 1.0) {
                    meta.multiplier = multiplier
                }

                item.itemMeta = meta
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePotionDelta(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = event.item
        val meta = item.itemMeta

        if (meta !is PotionMeta) {
            return
        }

        val multiplier = meta.multiplier
        if (multiplier == 1.0) {
            return
        }

        val type = meta.basePotionType ?: return
        val effects = type.potionEffects

        if (effects.isEmpty()) {
            return
        }

        // Apply multiplier to each effect's duration
        for (effect in effects) {
            val newDuration = (effect.duration * multiplier).toInt()
            player.addPotionEffect(
                PotionEffect(
                    effect.type,
                    newDuration,
                    effect.amplifier
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePotionDelta(event: PotionSplashEvent) {
        val entities = event.affectedEntities
        val item = event.potion.item

        val meta = item.itemMeta as? PotionMeta ?: return

        val multiplier = meta.multiplier
        if (multiplier == 1.0) {
            return
        }

        val type = meta.basePotionType ?: return
        val effects = type.potionEffects

        if (effects.isEmpty()) {
            return
        }

        // Apply multiplier to each effect's duration for each affected entity
        for (entity in entities) {
            val intensity = event.getIntensity(entity)

            for (effect in effects) {
                val newDuration = (effect.duration * multiplier * intensity).toInt()
                entity.addPotionEffect(
                    PotionEffect(
                        effect.type,
                        newDuration,
                        effect.amplifier
                    )
                )
            }
        }
    }

    private var PotionMeta.multiplier: Double
        get() = this.persistentDataContainer.getOrDefault(
            plugin.createNamespacedKey("duration_multiplier"),
            PersistentDataType.DOUBLE,
            1.0
        )
        set(value) {
            this.persistentDataContainer.set(
                plugin.createNamespacedKey("duration_multiplier"),
                PersistentDataType.DOUBLE,
                value
            )
        }
}
