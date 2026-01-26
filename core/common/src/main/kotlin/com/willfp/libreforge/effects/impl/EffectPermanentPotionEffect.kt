package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

@Suppress("UNCHECKED_CAST")
object EffectPermanentPotionEffect : Effect<NoCompileData>("permanent_potion_effect") {
    override val shouldReload = false

    override val arguments = arguments {
        require(
            "effect",
            "You must specify a valid potion effect! See here: " +
                    "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
            Config::getString
        ) {
            @Suppress("DEPRECATION")
            PotionEffectType.getByName(it.uppercase()) != null
        }
        require("level", "You must specify the effect level!")
    }

    private val metaKey = "libreforge_${this.id}"

    private const val DURATION = -1

    data class PotionEffectData(
        val effectType: PotionEffectType,
        val level: Int,
        val particles: Boolean,
        val icon: Boolean
    )

    private fun getHolderData(player: Player): MutableMap<UUID, PotionEffectData> {
        return player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, PotionEffectData>
            ?: mutableMapOf()
    }

    private fun refreshEffectsOfType(player: Player, type: PotionEffectType) {
        player.removePotionEffect(type)
        val active = getHolderData(player)
            .values
            .filter { it.effectType == type }
        if (active.isEmpty()) return
        val best = active.maxByOrNull { it.level }!!
        val effect = PotionEffect(
            type,
            DURATION,
            best.level,
            false,
            active.any { it.particles },
            active.any { it.icon }
        )
        player.addPotionEffect(effect)
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        plugin.server.scheduler.runTask(plugin, Runnable {
            val types = getHolderData(player)
                .values
                .map { it.effectType }
                .toSet()

            types.forEach { refreshEffectsOfType(player, it) }
            }
        )
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return

        @Suppress("DEPRECATION")
        val effectType = PotionEffectType.getByName(config.getString("effect").uppercase())
            ?: PotionEffectType.LUCK

        val level = config.getIntFromExpression("level", player) - 1
        val icon = config.getBoolOrNull("icon") ?: true
        val particles = config.getBoolOrNull("particles") ?: true

        val holderData = getHolderData(player)
        holderData[identifiers.uuid] = PotionEffectData(effectType, level, particles, icon)
        player.setMetadata(metaKey, plugin.metadataValueFactory.create(holderData))

        refreshEffectsOfType(player, effectType)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val holderData = getHolderData(player)
        val removed = holderData.remove(identifiers.uuid) ?: return
        player.setMetadata(metaKey, plugin.metadataValueFactory.create(holderData))

        refreshEffectsOfType(player, removed.effectType)
    }
}