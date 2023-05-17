package com.willfp.libreforge.entities.impl

import com.willfp.eco.core.price.ConfiguredPrice
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.entities.LibreforgeCustomEntity
import com.willfp.libreforge.plugin
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.inventory.ItemStack

class CustomEntityPricePickup(
    private val price: ConfiguredPrice,
    private val item: ItemStack,
    private val isGlowing: Boolean
) : LibreforgeCustomEntity(
    plugin.namespacedKeyFactory.create("custom_entity_price_pickup")
) {
    init {
        tryInitialize()
    }

    override fun matches(entity: Entity?): Boolean {
        if (entity == null) {
            return false
        }

        if (entity.hasMetadata(META_KEY)) {
            return false
        }

        return entity.getMetadata(META_KEY).firstOrNull()?.value() as? ConfiguredPrice == price
    }

    override fun spawn(location: Location): Entity {
        val item = location.world!!.dropItem(location, item)

        item.setMetadata(META_KEY, plugin.metadataValueFactory.create(price))
        item.isGlowing = isGlowing

        return item
    }

    companion object Handler : Listener {
        private const val META_KEY = "libreforge:price_pickup_entity"

        private var initialized = false

        @EventHandler
        fun onPickup(event: PlayerAttemptPickupItemEvent) {
            val item = event.item
            val player = event.player

            if (!item.hasMetadata(META_KEY)) {
                return
            }

            val price = item.getMetadata(META_KEY).firstOrNull()?.value() as? ConfiguredPrice ?: return
            event.isCancelled = true
            item.remove()
            price.giveTo(player)

            player.sendMessage(
                plugin.langYml.getString("picked-up-price-item")
                    .replace("%price%", price.getDisplay(player).formatEco(player))
            )
        }

        private fun tryInitialize() {
            if (initialized) {
                return
            } else {
                initialized = true
                plugin.eventManager.registerListener(this)
            }
        }
    }
}
