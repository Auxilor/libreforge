package com.willfp.libreforge.triggers

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

data class TriggerData @JvmOverloads constructor(
    val player: Player? = null,
    val victim: LivingEntity? = null,
    val block: Block? = null,
    val event: WrappedEvent<*>? = null,
    val location: Location? = victim?.location ?: player?.location,
    val projectile: Projectile? = null,
    val damageCause: EntityDamageEvent.DamageCause? = null,
    val velocity: Vector? = player?.velocity ?: victim?.velocity,
    val item: ItemStack? = player?.inventory?.itemInMainHand ?: victim?.equipment?.itemInMainHand,
    val text: String? = null,
    internal val originalPlayer: Player? = player
)

enum class TriggerParameter {
    PLAYER,
    VICTIM,
    BLOCK,
    EVENT,
    LOCATION,
    PROJECTILE,
    DAMAGE_CAUSE,
    VELOCITY,
    ITEM,
    TEXT
}
