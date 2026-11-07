package com.willfp.libreforge

import com.willfp.libreforge.slot.SlotItemProvidedHolder
import com.willfp.libreforge.slot.impl.SlotTypeOffhand
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.UseCooldownComponent

/**
 * The item whose cooldown group the player's use cooldown is read from and written to:
 * a stand-in item for [group] when given, otherwise the held item for [holder].
 */
internal fun Player.useCooldownItem(group: String?, holder: ProvidedHolder): ItemStack? =
    if (group == null) {
        heldCooldownItem(holder).takeUnless { it.type.isAir }
    } else {
        NamespacedKey.fromString(group.lowercase())?.let { groupItem(it) }
    }

/**
 * The full length in seconds of the cooldown in [group], read from the use_cooldown component
 * of a matching item the player holds or carries, or of the held item for [holder] with no [group].
 */
internal fun Player.useCooldownSeconds(group: String?, holder: ProvidedHolder): Float? {
    val item = if (group == null) {
        heldCooldownItem(holder)
    } else {
        val key = NamespacedKey.fromString(group.lowercase())
        inventory.contents.filterNotNull().firstOrNull { key != null && it.useCooldownGroup == key }
    }

    return item?.useCooldownComponent?.cooldownSeconds
}

private fun Player.heldCooldownItem(holder: ProvidedHolder): ItemStack {
    val offhand = inventory.itemInOffHand
    val isOffhandProvider = if (holder is SlotItemProvidedHolder<*>) {
        holder.slotType == SlotTypeOffhand
    } else {
        holder.provider == offhand
    }

    return if (isOffhandProvider && offhand.useCooldownComponent != null) offhand else inventory.itemInMainHand
}

private fun groupItem(group: NamespacedKey): ItemStack {
    val item = ItemStack(Material.STONE)
    val meta = item.itemMeta ?: return item
    val component = meta.useCooldown

    component.cooldownGroup = group
    meta.setUseCooldown(component)
    item.itemMeta = meta

    return item
}

private val ItemStack.useCooldownComponent: UseCooldownComponent?
    get() = itemMeta?.run { if (hasUseCooldown()) useCooldown else null }

private val ItemStack.useCooldownGroup: NamespacedKey?
    get() = useCooldownComponent?.let { it.cooldownGroup ?: type.key }
