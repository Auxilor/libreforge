package com.willfp.libreforge.effects.impl

import com.willfp.eco.util.duration
import com.willfp.libreforge.effects.templates.MultiplierEffect
import com.willfp.libreforge.plugin
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
        PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL, PotionType.AWKWARD,
        PotionType.MUNDANE, PotionType.THICK, PotionType.WATER
    )

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BrewEvent) {
        val player = event.contents.viewers.filterIsInstance<Player>().firstOrNull() ?: return

        val multiplier = getMultiplier(player)

        plugin.scheduler.run {
            for (i in 0..2) {
                val item = event.contents.getItem(i) ?: continue
                val meta = item.itemMeta as? PotionMeta ?: continue

                val potionData = meta.basePotionData

                if (potionData.type in cannotExtend) {
                    continue
                }

                val duration = potionData.duration
                val delta = (duration * multiplier).toInt() - duration

                meta.persistentDataContainer.set(
                    plugin.namespacedKeyFactory.create("duration-delta"),
                    PersistentDataType.INTEGER,
                    delta
                )

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
        val data = meta.basePotionData

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (data.type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOW] = 4
            effects[PotionEffectType.DAMAGE_RESISTANCE] = 2
        } else {
            val effectType = data.type.effectType ?: return
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
        val data = meta.basePotionData

        val effects = mutableMapOf<PotionEffectType, Int>()

        if (data.type == PotionType.TURTLE_MASTER) {
            effects[PotionEffectType.SLOW] = 4
            effects[PotionEffectType.DAMAGE_RESISTANCE] = 2
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

    private val PotionMeta.delta: Int
        get() = this.persistentDataContainer.getOrDefault(
            plugin.createNamespacedKey("duration_delta"),
            PersistentDataType.INTEGER,
            0
        )
}
