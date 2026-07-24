package com.willfp.libreforge.integrations.mythicmobs.impl.listener

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.effects.impl.EffectTelekinesis
import com.willfp.libreforge.plugin
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * MythicMobs drops never reach the death event's drop list, so the telekinesis
 * effect can't see them - this hands them to the killer instead.
 *
 * This used to be an Effect registered under the "telekinesis" id, which
 * overwrote the generic telekinesis effect in the registry whenever MythicMobs
 * was installed, silently disabling it for blocks, vanilla mobs and fishing.
 * The behaviour is unchanged, it's just no longer an effect: drops are only
 * taken over when the killer actually has telekinesis, so MythicMobs keeps
 * handling them in every other case.
 */
object MythicMobsTelekinesisListener : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun handle(event: MythicMobDeathEvent) {
        val victim = event.entity

        if (victim is Player && !plugin.configYml.getBool("effects.telekinesis.on-players")) {
            return
        }

        val player = when (val killer = event.killer) {
            is Player -> killer
            is Projectile -> killer.shooter as? Player
            is Tameable -> {
                if (!killer.isTamed || !EffectTelekinesis.allowTamedMobKills) return
                killer.owner as? Player
            }

            else -> null
        } ?: return

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        val drops = event.drops.filterNotNull()

        DropQueue(player)
            .addItems(drops)
            .setLocation(BukkitAdapter.adapt(event.mob.location))
            .forceTelekinesis()
            .push()

        event.drops.clear()
    }
}
