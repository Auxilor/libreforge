package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectInfiniteLavabucket : Effect<NoCompileData>("infinite_lavabucket") {

    private val activePlayers = mutableSetOf<UUID>()

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        val player = event.player
        if (player.uniqueId !in activePlayers) return
        if (event.bucket != Material.LAVA_BUCKET) return

        val slot = player.inventory.heldItemSlot

        plugin.server.scheduler.runTask(plugin, Runnable {
            val item = player.inventory.getItem(slot)
            if (item != null && item.type == Material.BUCKET) {
                player.inventory.setItem(slot, ItemStack(Material.LAVA_BUCKET))
            }
        })
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return
        activePlayers.add(player.uniqueId)
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