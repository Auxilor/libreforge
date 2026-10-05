package com.willfp.libreforge

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import com.willfp.eco.core.Prerequisite
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent
import org.bukkit.Bukkit
import org.bukkit.Registry
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRemoveEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.lang.reflect.Method

/**
 * Which dispatchers have a [HolderState], and when they are created and removed.
 */
internal object HolderLifecycle {
    /**
     * True while the shutdown sweep runs.
     */
    @Volatile
    var isSweeping = false

    /**
     * If the platform fires an add event for every entity, including NPC players.
     */
    val hasEntityAddEvent: Boolean
        get() = Prerequisite.HAS_PAPER.isMet

    /**
     * The listeners to register: the shared one and exactly one platform listener.
     */
    fun listeners(): List<Listener> = listOf(
        HolderLifecycleListener,
        if (Prerequisite.HAS_PAPER.isMet) PaperHolderLifecycleListener else SpigotHolderLifecycleListener
    )

    /**
     * If the server is stopping, so work deferred to a later tick would never run.
     */
    fun isStopping(): Boolean {
        if (isSweeping) {
            return true
        }

        return if (Prerequisite.HAS_PAPER.isMet) {
            PaperHolderLifecycleListener.isServerStopping()
        } else {
            SpigotHolderLifecycleListener.isServerStopping()
        }
    }
}

private val ECO_MODIFIER_PATTERN = Regex("\\d+_\\d+")

/**
 * Remove eco attribute modifiers left over from effects, matched on the modifier key.
 */
internal fun LivingEntity.removeEcoAttributeModifiers() {
    for (attribute in Registry.ATTRIBUTE) {
        val instance = this.getAttribute(attribute) ?: continue

        for (modifier in instance.modifiers.toList()) {
            if (modifier.key.namespace == "eco" && modifier.key.key.matches(ECO_MODIFIER_PATTERN)) {
                instance.removeModifier(modifier)
            }
        }
    }
}

internal object HolderLifecycleListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        HolderStates.signal(event.player.toDispatcher(), HolderChange.WorldChange)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPluginDisable(event: PluginDisableEvent) {
        // Every plugin is still enabled at this point, and players are not yet saved.
        if (event.plugin === plugin) {
            HolderStates.shutdownSweep()
        }
    }
}

internal object SpigotHolderLifecycleListener : Listener {
    private var minecraftServer: Any? = null
    private var hasStoppedMethod: Method? = null
    private var hasLookedUp = false

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntitiesLoad(event: EntitiesLoadEvent) {
        for (entity in event.entities) {
            if (entity is LivingEntity) {
                HolderStates.trackEntity(entity)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSpawn(event: EntitySpawnEvent) {
        val entity = event.entity as? LivingEntity ?: return
        HolderStates.trackEntity(entity)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRemove(event: EntityRemoveEvent) {
        val entity = event.entity as? LivingEntity ?: return
        HolderStates.untrackEntity(entity)

        // Looked up by name, as the cause does not exist in every API version.
        if (event.cause.name == "CHANGED_DIMENSION") {
            HolderStates.retrack(entity.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntitiesUnload(event: EntitiesUnloadEvent) {
        for (entity in event.entities) {
            if (entity is LivingEntity) {
                HolderStates.untrackEntity(entity)
            }
        }
    }

    /**
     * Spigot has no Bukkit API for this, so read CraftBukkit's MinecraftServer#hasStopped.
     */
    fun isServerStopping(): Boolean {
        if (!hasLookedUp) {
            hasLookedUp = true
            runCatching {
                val server = Bukkit.getServer().javaClass.getMethod("getServer").invoke(Bukkit.getServer())
                hasStoppedMethod = generateSequence<Class<*>>(server.javaClass) { it.superclass }
                    .firstNotNullOfOrNull { clazz ->
                        clazz.declaredMethods.firstOrNull { it.name == "hasStopped" && it.parameterCount == 0 }
                    }
                    ?.apply { isAccessible = true }
                minecraftServer = server
            }
        }

        val method = hasStoppedMethod ?: return false
        val server = minecraftServer ?: return false

        return runCatching { method.invoke(server) as? Boolean }.getOrNull() ?: false
    }
}

internal object PaperHolderLifecycleListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onAdd(event: EntityAddToWorldEvent) {
        val entity = event.entity as? LivingEntity ?: return
        HolderStates.trackEntity(entity)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onRemove(event: EntityRemoveFromWorldEvent) {
        val entity = event.entity as? LivingEntity ?: return
        HolderStates.untrackEntity(entity)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onEquipmentChange(event: EntityEquipmentChangedEvent) {
        val entity = event.entity
        if (entity.isRealPlayer) {
            return
        }

        if (event.equipmentChanges.values.all { isDamageOnly(it.oldItem(), it.newItem()) }) {
            return
        }

        HolderStates.signal(entity.toDispatcher(), HolderChange.Items)
    }

    // Durability loss on every hit of an armoured mob must not re-ask its providers.
    private fun isDamageOnly(oldItem: ItemStack, newItem: ItemStack): Boolean {
        if (oldItem.type != newItem.type || oldItem.amount != newItem.amount) {
            return false
        }

        val oldMeta = oldItem.itemMeta as? Damageable ?: return oldItem.isSimilar(newItem)
        val newMeta = newItem.itemMeta as? Damageable ?: return false

        oldMeta.damage = 0
        newMeta.damage = 0
        return oldMeta == newMeta
    }

    fun isServerStopping(): Boolean = Bukkit.isStopping()
}
