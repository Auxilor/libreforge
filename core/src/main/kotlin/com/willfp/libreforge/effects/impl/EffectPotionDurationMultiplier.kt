@file:Suppress("DEPRECATION", "REMOVAL")

package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.duration
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
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

object EffectPotionDurationMultiplier : MultiplierEffect("potion_duration_multiplier") {
    private val cannotExtend = setOf(
        PotionType.AWKWARD, PotionType.MUNDANE, PotionType.THICK, PotionType.WATER
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        val player = event.contents.viewers.filterIsInstance<Player>().firstOrNull() ?: return

        val multiplier = getMultiplier(player.toDispatcher())

        plugin.scheduler.run {
            for (i in 0..2) {
                val item = event.contents.getItem(i) ?: continue
                val meta = item.itemMeta as? PotionMeta ?: continue

                val potionData = meta.basePotionData ?: continue

                if (potionData.type in cannotExtend) {
                    continue
                }

                val duration = potionData.duration
                val delta = (duration * multiplier).toInt() - duration

                if (delta != 0) {
                    meta.delta = delta
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

        val delta = meta.delta
        val data = meta.basePotionData ?: return

        val type = data.type

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOWNESS] = 4
            effects[PotionEffectType.RESISTANCE] = 2
        } else {
            val effectType = type.effectType ?: return
            effects[effectType] = if (data.isUpgraded) 2 else 1
        }

        val newDuration = data.duration + delta

        for ((k, level) in effects) {
            player.addPotionEffect(
                PotionEffect(
                    k,
                    newDuration,
                    level - 1
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handlePotionDelta(event: PotionSplashEvent) {
        val entities = event.affectedEntities
        val item = event.potion.item

        val meta = item.itemMeta as? PotionMeta ?: return
        val data = meta.basePotionData ?: return

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (data.type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOWNESS] = 4
            effects[PotionEffectType.RESISTANCE] = 2
        } else {
            effects[data.type.effectType ?: return] = if (data.isUpgraded) 2 else 1
        }

        for (entity in entities) {
            val newDuration = (data.duration + meta.delta) * event.getIntensity(entity)

            for ((key, value) in effects) {
                entity.addPotionEffect(
                    PotionEffect(
                        key,
                        newDuration.toInt(),
                        value - 1
                    )
                )
            }
        }
    }

    private var PotionMeta.delta: Int
        get() = this.persistentDataContainer.getOrDefault(
            plugin.createNamespacedKey("duration_delta"),
            PersistentDataType.INTEGER,
            0
        )
        set(value) {
            this.persistentDataContainer.set(
                plugin.createNamespacedKey("duration_delta"),
                PersistentDataType.INTEGER,
                value
            )
        }
}
