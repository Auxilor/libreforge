package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.plugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectInfiniteBucket : Effect<Set<String>>("infinite_bucket") {

    override val arguments = arguments {
        require(listOf("type", "types"), "You must specify the bucket type(s) (e.g. type: any, or types: [lava, water, axolotl, etc.])!")
    }

    private val activePlayers = mutableMapOf<UUID, Set<String>>()

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val allowedTypes = activePlayers[player.uniqueId] ?: return
        if (allowedTypes.isNotEmpty() && event.bucket.name !in allowedTypes) return

        val slot = player.inventory.heldItemSlot

        plugin.scheduler.runAsync {
            val item = player.inventory.getItem(slot)
            if (item != null && item.type == Material.BUCKET) {
                player.inventory.setItem(slot, ItemStack(event.bucket))
            }
        }
    }

    @EventHandler
    fun onMilkConsume(event: PlayerItemConsumeEvent) {
        val player = event.player
        val allowedTypes = activePlayers[player.uniqueId] ?: return
        if (event.item.type != Material.MILK_BUCKET) return
        if (allowedTypes.isNotEmpty() && "MILK_BUCKET" !in allowedTypes) return

        val slot = player.inventory.heldItemSlot

        plugin.scheduler.runAsync {
            val item = player.inventory.getItem(slot)
            if (item != null && item.type == Material.BUCKET) {
                player.inventory.setItem(slot, ItemStack(Material.MILK_BUCKET))
            }
        }
    }

    override fun makeCompileData(config: Config, context: ViolationContext): Set<String> {
        val types = config.getStrings("types", "type")
        return if (types.size == 1 && types[0].equals("any", ignoreCase = true)) {
            emptySet()
        } else {
            types.map { it.uppercase() + "_BUCKET" }.toSet()
        }
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: Set<String>
    ) {
        val player = dispatcher.get<Player>() ?: return
        activePlayers[player.uniqueId] = compileData
    }

    override fun onDisable(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder
    ) {
        val player = dispatcher.get<Player>() ?: return
        activePlayers.remove(player.uniqueId)
    }
}
