package com.willfp.libreforge.triggers

import com.willfp.eco.core.items.HashedItem
import com.willfp.libreforge.EmptyProvidedHolder
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.triggers.impl.TriggerBlank
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Objects

data class TriggerData(
    /*
    In order to get the holder from the trigger data without
    having to pass it around everywhere, we just pass it in
    Trigger#dispatch by copying over the trigger data.
     */
    val holder: ProvidedHolder = EmptyProvidedHolder,

    val player: Player? = null,
    val victim: LivingEntity? = null,
    val block: Block? = null,
    val event: Event? = null,
    val location: Location? = victim?.location ?: player?.location,
    val projectile: Projectile? = null,
    val velocity: Vector? = player?.velocity ?: victim?.velocity,
    val item: ItemStack? = player?.inventory?.itemInMainHand ?: victim?.equipment?.itemInMainHand,
    val text: String? = null,
    val value: Double = 1.0,

    /*
    This is a bodge inherited from v3, but it's the only real way to do this.
    Essentially, the player can get messed up by mutators, and that causes
    placeholders to parse incorrectly when doing Config#get<x>FromExpression.

    It's really not very nice, but it's good enough. Just don't think about it.
     */
    internal val _originalPlayer: Player? = player
) {
    val foundItem: ItemStack?
        get() = holder.getProvider() ?: item

    /**
     * Turn into a dispatched trigger for a [player].
     */
    fun dispatch(player: Player) = DispatchedTrigger(
        player,
        TriggerBlank,
        this
    )

    override fun hashCode(): Int {
        return Objects.hash(
            holder,
            player,
            victim,
            block,
            event,
            location,
            projectile,
            velocity,
            item?.let { HashedItem.of(it) },
            text,
            value
        )
    }

    override fun equals(other: Any?): Boolean {
        return other is TriggerData && other.hashCode() == this.hashCode()
    }
}

enum class TriggerParameter(
    vararg val inheritsFrom: TriggerParameter
) {
    PLAYER,
    VICTIM,
    BLOCK,
    EVENT,
    LOCATION(VICTIM, PLAYER),
    PROJECTILE,
    VELOCITY(PLAYER, VICTIM),
    ITEM(PLAYER, VICTIM),
    TEXT,
    VALUE
}
