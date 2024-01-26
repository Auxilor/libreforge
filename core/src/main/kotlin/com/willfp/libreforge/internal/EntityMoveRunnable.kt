package com.willfp.libreforge.internal

import com.willfp.libreforge.internal.api.AsyncEntityMoveEvent
import com.willfp.libreforge.internal.api.AsyncPlayerMoveEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class EntityMoveRunnable : Runnable {

    private val ENTITY_CACHE: MutableMap<UUID, Location> = HashMap()

    override fun run() {
        for (world in Bukkit.getWorlds()) {
            for (entity in world.entities) {
                if (entity !is LivingEntity) {
                    continue
                }

                val currentLocation = entity.location
                if (!ENTITY_CACHE.containsKey(entity.uniqueId)) {
                    ENTITY_CACHE[entity.uniqueId] = currentLocation
                    continue
                }

                val lastLocation = ENTITY_CACHE[entity.getUniqueId()]!!

                if (locationSoftEquals(currentLocation, lastLocation)) {
                    continue
                }

                if (entity is Player) {
                    val event = AsyncPlayerMoveEvent(lastLocation, currentLocation, entity)
                    Bukkit.getPluginManager().callEvent(event)
                    if (event.isCancelled) {
                        entity.teleport(lastLocation)
                    }
                }

                val event = AsyncEntityMoveEvent(lastLocation, currentLocation, entity)
                Bukkit.getPluginManager().callEvent(event)
                if (event.isCancelled) {
                    entity.teleport(lastLocation)
                }
            }
        }

        for (key in ENTITY_CACHE.keys) {
            val entity = Bukkit.getEntity(key)
            if (entity == null || entity.isDead || !entity.isValid) {
                ENTITY_CACHE.remove(key)
            }
        }
    }

    private fun locationSoftEquals(location: Location, lastLocation: Location): Boolean {
        return location.getX() == lastLocation.getX() && location.getY() == lastLocation.getY()
                && location.getZ() == lastLocation.getZ() && location.getPitch() == lastLocation.getPitch()
                && location.getYaw() == lastLocation.getYaw() && location.getWorld().uid == lastLocation.getWorld().uid
    }
}