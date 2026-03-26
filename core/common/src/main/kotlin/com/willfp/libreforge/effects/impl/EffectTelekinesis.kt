package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import io.papermc.paper.event.block.PlayerShearBlockEvent
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import java.util.UUID

object EffectTelekinesis : Effect<NoCompileData>("telekinesis") {
    private val players = listMap<UUID, UUID>()
    private var allowTamedMobKills: Boolean = false

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        players[dispatcher.uuid].add(identifiers.uuid)
        allowTamedMobKills = config.getBoolOrNull("on_tamed_mob_kills") ?: false
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        players[dispatcher.uuid].remove(identifiers.uuid)
    }

    override fun postRegister() {
        TelekinesisUtils.registerTest { players[it.uniqueId].isNotEmpty() }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: BlockDropItemEvent) {
        val player = event.player
        val block = event.block

        if (!plugin.configYml.getBool("effects.telekinesis.always-process-blocks")
            && !TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val drops = event.items.map { it.itemStack }
        event.items.clear()

        DropQueue(player)
            .setLocation(block.location)
            .addItems(drops)
            .push()
    }

    @EventHandler(
        priority = EventPriority.HIGH,
        ignoreCancelled = true
    )
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        // Filter out telekinesis spawner xp to prevent dupe
        if (block.type == Material.SPAWNER) {
            event.expToDrop = 0
        }

        DropQueue(player)
            .setLocation(block.location)
            .addXP(event.expToDrop)
            .push()

        event.expToDrop = 0
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: EntityDeathByEntityEvent) {
        val victim = event.victim


        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            if (MythicBukkit.inst().mobManager.isMythicMob(victim)) {
                return
            }
        }

        if (victim is Player && plugin.configYml.getBool("effects.telekinesis.on-players")) {
            return
        }

        val player = when (val killer = event.killer) {
            is Player -> killer
            is Projectile -> killer.shooter as? Player
            is Tameable -> {
                if (!killer.isTamed || !allowTamedMobKills) return
                killer.owner as? Player
            }
            else -> null
        } ?: return

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        val xp = event.xp
        val drops = event.drops.filterNotNull()

        DropQueue(player)
            .setLocation(victim.location)
            .addItems(drops)
            .addXP(xp)
            .forceTelekinesis()
            .push()

        event.deathEvent.droppedExp = 0
        event.deathEvent.drops.clear()
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerShearEntityEvent) {
        val victim = event.entity


        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            if (MythicBukkit.inst().mobManager.isMythicMob(victim)) {
                return
            }
        }


        val player = event.player

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        val drops = event.drops.toList()

        if (drops.isEmpty()) {
            return
        }

        DropQueue(player)
            .setLocation(victim.location)
            .addItems(drops)
            .forceTelekinesis()
            .push()

        event.drops.clear()
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerShearBlockEvent) {
        val player = event.player
        val block = event.block

        if (!plugin.configYml.getBool("effects.telekinesis.always-process-blocks")
            && !TelekinesisUtils.testPlayer(player)) {
            return
        }

        if (!AntigriefManager.canBreakBlock(player, block)) {
            return
        }

        val drops = event.drops.toList()

        if (drops.isEmpty()) {
            return
        }

        DropQueue(player)
            .setLocation(block.location)
            .addItems(drops)
            .push()

        event.drops.clear()
    }

    // Fires at HIGH so TriggerCatchFish (NORMAL) has already applied modifiers
    // and updated the caught entity's stack before we read it.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun handle(event: PlayerFishEvent) {
        if (event.state != PlayerFishEvent.State.CAUGHT_FISH) return

        val player = event.player

        if (!TelekinesisUtils.testPlayer(player)) {
            return
        }

        val caught = event.caught as? org.bukkit.entity.Item ?: return

        // getItemStack() returns a defensive copy - that's fine, we just need the data.
        val stack = caught.itemStack

        DropQueue(player)
            .setLocation(event.hook.location)
            .addItems(listOf(stack))
            .forceTelekinesis()
            .push()

        // Remove the entity so it doesn't also fly to the player naturally.
        caught.remove()
    }
}