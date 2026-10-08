package com.willfp.libreforge

import com.willfp.eco.core.Eco
import com.willfp.eco.core.FoliaSupport
import com.willfp.eco.core.Prerequisite
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.math.ceil

/**
 * The one place libreforge decides which thread may touch what.
 *
 * Call sites are the same on every platform; only the answers differ. Off Folia there is one
 * main thread that owns everything, so [runOwned] is always inline and nothing changes. On Folia
 * each region owns its own entities and blocks, and the global region owns server-wide state.
 *
 * Paper-only `Bukkit` methods are only reached behind [isFolia], so Spigot never resolves them.
 */
internal object Regions {
    /**
     * If the server is Folia.
     */
    val isFolia = Prerequisite.HAS_FOLIA.isMet

    /**
     * If the current thread may touch [entity].
     *
     * Off Folia this is the main thread, not eco's `isOwnedByCurrentRegion`, which is `true`
     * on async threads too. Use this to decide between a live read and a cached one.
     */
    fun owns(entity: Entity): Boolean =
        if (isFolia) Bukkit.isOwnedByCurrentRegion(entity) else Bukkit.isPrimaryThread()

    /**
     * If the current thread may touch the blocks at [location].
     */
    fun owns(location: Location): Boolean =
        if (isFolia) Bukkit.isOwnedByCurrentRegion(location) else Bukkit.isPrimaryThread()

    /**
     * If the current thread may touch server-wide state.
     */
    fun ownsGlobal(): Boolean =
        if (isFolia) Bukkit.isGlobalTickThread() else Bukkit.isPrimaryThread()

    /**
     * If the current thread may touch [dispatcher]: its entity, else its location, else
     * server-wide state.
     */
    fun owns(dispatcher: Dispatcher<*>): Boolean {
        val target = dispatcher.dispatcher
        if (target is Entity) {
            return owns(target)
        }

        val location = dispatcher.location ?: return ownsGlobal()
        return owns(location)
    }

    /**
     * If [location] can be touched from here, for skipping blocks and entities in other regions
     * during area scans. Always `true` off Folia, so scans are unchanged there.
     */
    fun canReach(location: Location): Boolean =
        Eco.get().isOwnedByCurrentRegion(location)

    /**
     * If every chunk within [radius] blocks of [center] can be touched from here, for area scans
     * such as `getNearbyEntities`, which Folia refuses when they reach into another region.
     * Always `true` off Folia.
     */
    fun canReach(center: Location, radius: Double): Boolean {
        if (!isFolia) {
            return true
        }

        if (center.world == null) {
            return false
        }

        val radiusChunks = ceil(radius / 16).toInt() + 1
        return Bukkit.isOwnedByCurrentRegion(center, radiusChunks)
    }

    /**
     * The longest distance up to [distance] that a ray or scan from [origin] can cover without
     * leaving this region. Always [distance] off Folia.
     */
    fun reachableDistance(origin: Location, distance: Double): Double {
        var reachable = distance

        while (reachable >= 1 && !canReach(origin, reachable)) {
            reachable /= 2
        }

        return if (reachable >= 1) reachable else 0.0
    }

    /**
     * If [entity] can be touched from here. Always `true` off Folia.
     */
    fun canReach(entity: Entity): Boolean =
        Eco.get().isOwnedByCurrentRegion(entity)

    /**
     * Run [block] on the region owning [entity]: now if this thread owns it, otherwise on the
     * entity's next tick. Always inline off Folia, as before.
     */
    inline fun runOwned(entity: Entity, crossinline block: () -> Unit) {
        if (Eco.get().isOwnedByCurrentRegion(entity)) {
            block()
        } else {
            plugin.scheduler.on(entity).run { block() }
        }
    }

    /**
     * Run [block] on the region owning [location]. Always inline off Folia.
     */
    inline fun runOwned(location: Location, crossinline block: () -> Unit) {
        if (Eco.get().isOwnedByCurrentRegion(location)) {
            block()
        } else {
            plugin.scheduler.at(location).run { block() }
        }
    }

    /**
     * Run [block] on the global region. Always inline off Folia.
     */
    inline fun runGlobal(crossinline block: () -> Unit) {
        if (!isFolia || Bukkit.isGlobalTickThread()) {
            block()
        } else {
            plugin.scheduler.global().run { block() }
        }
    }

    /**
     * Run [block] on the region owning [dispatcher]. Always inline off Folia.
     */
    inline fun runOwned(dispatcher: Dispatcher<*>, crossinline block: () -> Unit) {
        val target = dispatcher.dispatcher
        if (target is Entity) {
            runOwned(target, block)
            return
        }

        val location = dispatcher.location
        if (location != null) {
            runOwned(location, block)
        } else {
            runGlobal(block)
        }
    }

    /**
     * Teleport [entity]. Folia has no synchronous teleport, so it moves on a later tick there
     * and this returns `true` once scheduled.
     */
    fun teleport(entity: Entity, location: Location): Boolean {
        if (isFolia) {
            entity.teleportAsync(location)
            return true
        }

        return entity.teleport(location)
    }

    /**
     * If [feature] is unavailable here, logging it once. Always `false` off Folia.
     */
    fun isUnsupported(feature: String): Boolean =
        FoliaSupport.isUnsupported(feature)
}
