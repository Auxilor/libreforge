package com.willfp.libreforge.integrations.mythicmobs.impl.effect

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import io.lumine.mythic.bukkit.BukkitAdapter
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import java.util.UUID

object EffectMythicTelekinesis : Effect<NoCompileData>("telekinesis") {
    override val description = "Causes MythicMobs mob drops to go directly into the player's inventory instead of dropping on the ground."
    override val categories = setOf("inventory")

    override val arguments = arguments {
        optional(
            "on_tamed_mob_kills",
            description = "Whether telekinesis also applies when the kill was made by the player's tamed mob. Defaults to false.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

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
    fun handle(event: MythicMobDeathEvent) {
        val victim = event.mob

        if (victim is Player && !plugin.configYml.getBool("effects.telekinesis.on-players")) {
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

        val drops = event.drops.filterNotNull()

        DropQueue(player)
            .addItems(drops)
            .setLocation(BukkitAdapter.adapt(victim.location))
            .forceTelekinesis()
            .push()

        event.drops.clear()
    }
}