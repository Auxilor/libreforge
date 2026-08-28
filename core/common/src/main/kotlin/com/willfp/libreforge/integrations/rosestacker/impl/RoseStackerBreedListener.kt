package com.willfp.libreforge.integrations.rosestacker.impl

import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.impl.TriggerBreed
import dev.rosewood.rosestacker.api.RoseStackerAPI
import dev.rosewood.rosestacker.config.SettingKey
import org.bukkit.entity.Ageable
import org.bukkit.entity.Animals
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack

/**
 * RoseStacker breeds an entire stack at once (cumulative breeding), which it does by cancelling
 * the interaction and spawning the offspring itself 30 ticks later. It only calls EntityBreedEvent
 * for them if it's been configured to, so without this listener the breed trigger doesn't fire.
 */
object RoseStackerBreedListener : Listener {
    // The amount of experience RoseStacker drops per offspring.
    private const val EXPERIENCE_PER_OFFSPRING = 7.0

    private const val EXPIRY_TICKS = 100L
    private const val MAX_SPAWN_DISTANCE_SQUARED = 64.0

    private class PendingBreed(
        val player: Player,
        val parent: Animals,
        val bredWith: ItemStack
    )

    private val pending = mutableListOf<PendingBreed>()

    // Captured before RoseStacker consumes the breeding item at EventPriority.HIGH, then read
    // back at MONITOR once it's known whether RoseStacker has taken the breeding over.
    private var captured: PendingBreed? = null

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun capture(event: PlayerInteractEntityEvent) {
        captured = null

        val animal = event.rightClicked as? Animals ?: return

        if (!animal.canBreed()) {
            return
        }

        val stacked = RoseStackerAPI.getInstance().getStackedEntity(animal) ?: return

        if (stacked.stackSize < 2) {
            return
        }

        val item = event.player.inventory.getItem(event.hand) ?: return

        if (!stacked.stackSettings.entityTypeData.isValidBreedingMaterial(item.type)) {
            return
        }

        captured = PendingBreed(event.player, animal, item.clone().apply { amount = 1 })
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun track(event: PlayerInteractEntityEvent) {
        val breed = captured ?: return
        captured = null

        // RoseStacker only cancels the interaction when it takes the breeding over itself.
        if (!event.isCancelled) {
            return
        }

        // If RoseStacker calls EntityBreedEvent then the breed trigger already fires normally.
        if (callsBreedEvent()) {
            return
        }

        pending += breed
        plugin.scheduler.runLater(EXPIRY_TICKS) { pending -= breed }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: CreatureSpawnEvent) {
        val offspring = event.entity

        if ((offspring as? Ageable)?.isAdult != false) {
            return
        }

        val breed = pending.firstOrNull {
            it.parent.type == offspring.type
                    && it.parent.world == offspring.world
                    && it.parent.location.distanceSquared(offspring.location) <= MAX_SPAWN_DISTANCE_SQUARED
        } ?: return

        TriggerBreed.force(breed.player, offspring, breed.bredWith, EXPERIENCE_PER_OFFSPRING)
    }

    private fun callsBreedEvent() = runCatching {
        SettingKey.ENTITY_CUMULATIVE_BREEDING_TRIGGER_BREED_EVENT.get()
    }.getOrDefault(false)
}
