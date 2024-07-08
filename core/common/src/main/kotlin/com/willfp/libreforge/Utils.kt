package com.willfp.libreforge

import com.willfp.eco.core.items.Items
import com.willfp.libreforge.proxy.Proxy
import com.willfp.libreforge.proxy.loadProxy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.math.roundToInt

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }
}

val Any.deprecationMessage: String?
    get() {
        val annotation = this::class.java.getAnnotation(Deprecated::class.java)
        return annotation?.message
    }

fun Location.getNearbyBlocks(
    x: Double,
    y: Double,
    z: Double
): Collection<Block> {
    val blocks = mutableListOf<Block>()

    val xRadius = (x / 2).roundToInt()
    val yRadius = (y / 2).roundToInt()
    val zRadius = (z / 2).roundToInt()

    for (xPos in -xRadius..xRadius) {
        for (yPos in -yRadius..yRadius) {
            for (zPos in -zRadius..zRadius) {
                blocks.add(this.clone().add(xPos.toDouble(), yPos.toDouble(), zPos.toDouble()).block)
            }
        }
    }

    return blocks
}

fun Location.getNearbyBlocksInSphere(
    radius: Double
): Collection<Block> = getNearbyBlocks(radius, radius, radius)
    .filter { it.location.distanceSquared(this) <= radius * radius }

fun Collection<ItemStack?>.filterNotEmpty() =
    this.filterNot { Items.isEmpty(it) }
        .filterNotNull()

internal val ItemStack?.isEcoEmpty: Boolean
    get() = Items.isEmpty(this)

fun ItemStack.applyDamage(damage: Int, player: Player?): Boolean {
    val meta = this.itemMeta as? Damageable ?: return false
    meta.damage += damage
    if (meta.damage >= this.type.maxDurability) {
        meta.damage = this.type.maxDurability.toInt()
        this.itemMeta = meta
        if (player != null) {
            Bukkit.getPluginManager().callEvent(PlayerItemBreakEvent(player, this))
            player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1f, 1f)
        }
        @Suppress("DEPRECATION")
        this.type = Material.AIR
    } else {
        this.itemMeta = meta
    }

    return true
}

// 1.21 compat
fun getEnchantment(id: String): Enchantment? =
    Enchantment.getByKey(NamespacedKey.minecraft(id))

@Proxy("OpenInventoryAccessorImpl")
interface OpenInventoryAccessor {
    fun getTopInventory(player: Player): Inventory
    fun getTopInventory(event: CraftItemEvent): Inventory
    fun getBottomInventory(event: CraftItemEvent): Inventory
}

val Player.topInventory: Inventory
    get() = loadProxy(OpenInventoryAccessor::class.java).getTopInventory(this)

val CraftItemEvent.topInventory: Inventory
    get() = loadProxy(OpenInventoryAccessor::class.java).getTopInventory(this)

val CraftItemEvent.bottomInventory: Inventory
    get() = loadProxy(OpenInventoryAccessor::class.java).getBottomInventory(this)
