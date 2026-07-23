package com.willfp.libreforge.drops

import com.willfp.eco.core.drops.DropQueue
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.event.DropCause
import com.willfp.libreforge.triggers.event.DropContext
import com.willfp.libreforge.triggers.event.EditableDropEvent
import com.willfp.libreforge.triggers.impl.TriggerBlockItemDrop
import com.willfp.libreforge.triggers.impl.TriggerCatchFish
import com.willfp.libreforge.triggers.impl.TriggerEntityItemDrop
import com.willfp.libreforge.triggers.impl.TriggerShear
import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Cancellable
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CopyOnWriteArrayList

/**
 * The result of running drops through the pipeline.
 */
data class DropOutcome(
    val items: List<ItemStack>,
    val xp: Int,
    val isCancelled: Boolean
)

/**
 * Extra drops contributed to a pipeline.
 */
data class DropContribution(
    val items: List<ItemStack> = emptyList(),
    val xp: Int = 0
) {
    companion object {
        val EMPTY = DropContribution()
    }
}

/**
 * Contributes extra drops to a pipeline, for example custom loot injected into
 * a vanilla block break.
 */
fun interface DropContributor {
    fun contribute(
        cause: DropCause,
        context: DropContext,
        location: Location
    ): DropContribution
}

/**
 * Runs drops through the libreforge drop pipeline, so that drops produced
 * outside of vanilla events are still subject to drop effects
 * (multiply_drops, cancel_drops, autosmelt, silk_touch, sell_items, ...).
 */
object LibreforgeDrops {
    private val contributors = CopyOnWriteArrayList<DropContributor>()

    /**
     * Register a contributor, called whenever a drop pipeline is built.
     */
    @JvmStatic
    fun registerContributor(contributor: DropContributor) {
        contributors += contributor
    }

    /**
     * Extra drops for a pipeline, gathered from all registered contributors.
     * A contributor that throws is logged and skipped, never allowed to break
     * the pipeline.
     */
    @JvmStatic
    fun contributions(
        cause: DropCause,
        context: DropContext,
        location: Location
    ): DropContribution {
        if (contributors.isEmpty()) {
            return DropContribution.EMPTY
        }

        val items = mutableListOf<ItemStack>()
        var xp = 0

        for (contributor in contributors) {
            runCatching { contributor.contribute(cause, context, location) }
                .onSuccess {
                    items += it.items
                    xp += it.xp
                }
                .onFailure { plugin.logger.warning("A drop contributor threw an exception: ${it.message}") }
        }

        return DropContribution(items, xp)
    }

    /**
     * Run [drops] through the pipeline, returning what survived it.
     *
     * Does not deliver the drops - see [dropAll] for that.
     */
    @JvmStatic
    @JvmOverloads
    fun process(
        drops: Collection<ItemStack>,
        cause: DropCause,
        context: DropContext,
        location: Location,
        xp: Int = 0,
        cancellable: Cancellable? = null,
        dispatchTrigger: Boolean = true
    ): DropOutcome {
        val contributed = contributions(cause, context, location)
        val initialDrops = drops + contributed.items

        val event = EditableDropEvent(
            initialDrops = initialDrops,
            cause = cause,
            context = context,
            dropLocation = location,
            cancellable = cancellable
        )

        val player = context.player
        val trigger = if (dispatchTrigger) cause.trigger else null

        if (trigger != null && player != null) {
            trigger.dispatch(
                player.toDispatcher(),
                TriggerData(
                    player = player,
                    block = context.block,
                    victim = context.entity as? LivingEntity,
                    location = location,
                    event = event,
                    value = initialDrops.sumOf { it.amount }.toDouble()
                )
            )
        }

        if (event.isCancelled) {
            return DropOutcome(emptyList(), 0, true)
        }

        // Read once: reading applies the accumulated modifiers in place, so a
        // second read would apply them twice.
        val results = event.items

        return DropOutcome(
            event.drops.toList(),
            xp + contributed.xp + results.sumOf { it.xp },
            false
        )
    }

    /**
     * Run [drops] through the pipeline and deliver whatever survives it.
     *
     * Drops go through a DropQueue when there is a player, which makes them
     * telekinesis- and antigrief-aware; otherwise they drop in the world.
     */
    @JvmStatic
    @JvmOverloads
    fun dropAll(
        drops: Collection<ItemStack>,
        cause: DropCause,
        context: DropContext,
        location: Location,
        xp: Int = 0,
        cancellable: Cancellable? = null
    ) {
        val outcome = process(drops, cause, context, location, xp, cancellable)

        if (outcome.isCancelled) {
            return
        }

        val player = context.player

        if (player != null) {
            DropQueue(player)
                .addItems(outcome.items)
                .addXP(outcome.xp)
                .setLocation(location)
                .push()
            return
        }

        val world = location.world ?: return
        outcome.items.forEach { world.dropItemNaturally(location, it) }
        if (outcome.xp > 0) {
            world.spawn(location, ExperienceOrb::class.java).experience = outcome.xp
        }
    }

    private val DropCause.trigger: Trigger?
        get() = when (this) {
            DropCause.BLOCK -> TriggerBlockItemDrop
            DropCause.ENTITY -> TriggerEntityItemDrop
            DropCause.FISHING -> TriggerCatchFish
            DropCause.SHEAR -> TriggerShear
            DropCause.CUSTOM -> null
        }
}
