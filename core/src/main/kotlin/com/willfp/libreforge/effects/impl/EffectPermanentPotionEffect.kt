package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

@Suppress("UNCHECKED_CAST")
object EffectPermanentPotionEffect : Effect<NoCompileData>("permanent_potion_effect") {
    override val arguments = arguments {
        require(
            "effect",
            "You must specify a valid potion effect! See here: " +
                    "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
            Config::getString
        ) {
            PotionEffectType.getByName(it.uppercase()) != null
        }
        require("level", "You must specify the effect level!")
    }

    private val metaKey = "libreforge_${this.id}"

    private val duration = if (Prerequisite.HAS_1_19_4.isMet) {
        -1
    } else {
        1_500_000_000
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        for ((_, pair) in meta) {
            val (effectType, level) = pair

            val effect = PotionEffect(
                effectType,
                duration,
                level,
                false,
                false,
                true
            )

            player.addPotionEffect(effect)
        }
    }

    override fun onEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val effectType = PotionEffectType.getByName(config.getString("effect").uppercase())
            ?: PotionEffectType.INCREASE_DAMAGE

        val level = config.getIntFromExpression("level", player) - 1

        val effect = PotionEffect(
            effectType,
            duration,
            level,
            false,
            false,
            true
        )

        player.addPotionEffect(effect)

        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        meta[identifiers.uuid] = Pair(effectType, level)

        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        val (toRemove, _) = meta[identifiers.uuid] ?: return

        val active = player.getPotionEffect(toRemove) ?: return

        if (Prerequisite.HAS_1_19_4.isMet) {
            if (active.duration != -1) {
                return
            }
        } else {
            if (active.duration < 1_000_000_000) {
                return
            }
        }

        meta.remove(identifiers.uuid)
        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))

        player.removePotionEffect(toRemove)
    }
}
