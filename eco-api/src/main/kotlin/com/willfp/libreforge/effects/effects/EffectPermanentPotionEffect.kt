package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

@Suppress("UNCHECKED_CAST")
class EffectPermanentPotionEffect : Effect("permanent_potion_effect") {
    private val metaKey = "${plugin.name}_${this.id}"

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        for ((_, pair) in meta) {
            val (effectType, level) = pair

            val effect = PotionEffect(
                effectType,
                1_500_000_000,
                level,
                plugin.configYml.getBool("potions.ambient.permanent"),
                plugin.configYml.getBool("potions.particles.permanent"),
                plugin.configYml.getBool("potions.icon.permanent")
            )

            player.addPotionEffect(effect)
        }
    }

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val effectType = PotionEffectType.getByName(config.getString("effect").uppercase())
            ?: PotionEffectType.INCREASE_DAMAGE

        val level = config.getIntFromExpression("level", player) - 1

        val effect = PotionEffect(
            effectType,
            1_500_000_000,
            level,
            plugin.configYml.getBool("potions.ambient.permanent"),
            plugin.configYml.getBool("potions.particles.permanent"),
            plugin.configYml.getBool("potions.icon.permanent")
        )

        player.addPotionEffect(effect)

        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        meta[identifiers.uuid] = Pair(effectType, level)

        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, Pair<PotionEffectType, Int>> ?: mutableMapOf()

        val (toRemove, _) = meta[identifiers.uuid] ?: return

        val active = player.getPotionEffect(toRemove) ?: return

        if (active.duration < 1_000_000_000) {
            return
        }

        meta.remove(identifiers.uuid)
        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))

        player.removePotionEffect(toRemove)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (PotionEffectType.getByName(config.getStringOrNull("effect")?.uppercase() ?: "") == null) violations.add(
            ConfigViolation(
                "effect",
                "You must specify the potion effect / invalid effect specified! Get a list of valid effects here: "
                        + " https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
            )
        )

        if (!config.has("level")) violations.add(
            ConfigViolation(
                "level",
                "You must specify the effect level!"
            )
        )

        return violations
    }
}