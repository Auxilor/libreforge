package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforge
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.getEffectAmount
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID

@Suppress("UNCHECKED_CAST")
class EffectPermanentPotionEffect : Effect("permanent_potion_effect") {
    private val metaKey = "${LibReforge.plugin.name}_${this.id}"

    override fun handleEnable(player: Player, config: Config) {
        val effectType = PotionEffectType.getByName(config.getString("effect").uppercase())
            ?: PotionEffectType.INCREASE_DAMAGE

        val effect = PotionEffect(
            effectType,
            1_500_000_000,
            config.getInt("level") - 1,
            false,
            false,
            true
        )

        player.addPotionEffect(effect)

        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, PotionEffectType> ?: mutableMapOf()

        meta[this.getUUID(player.getEffectAmount(this))] = effectType

        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))
    }

    override fun handleDisable(player: Player) {
        val meta = player.getMetadata(metaKey).firstOrNull()?.value()
                as? MutableMap<UUID, PotionEffectType> ?: mutableMapOf()

        val toRemove = meta[this.getUUID(player.getEffectAmount(this))] ?: return

        val active = player.getPotionEffect(toRemove) ?: return

        if (active.duration < 1_000_000_000) {
            return
        }

        meta.remove(this.getUUID(player.getEffectAmount(this)))
        player.setMetadata(metaKey, plugin.metadataValueFactory.create(meta))

        player.removePotionEffect(toRemove)
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        config.getStringOrNull("effect")
            ?: violations.add(
                ConfigViolation(
                    "effect",
                    "You must specify the potion effect!"
                )
            )

        config.getIntOrNull("level")
            ?: violations.add(
                ConfigViolation(
                    "level",
                    "You must specify the effect level!"
                )
            )

        return violations
    }
}