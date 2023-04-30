package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.plugin
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
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

    private val storageKey = NamespacedKey(plugin, "libreforge_${this.id}")

    private val duration = if (Prerequisite.HAS_1_19_4.isMet) {
        -1
    } else {
        1_500_000_000
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player

        val meta = player.pdc.get(storageKey, Storage) ?: mutableMapOf()

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

        val meta = player.pdc.get(storageKey, Storage) ?: mutableMapOf()

        meta[identifiers.uuid] = Pair(effectType, level)

        player.pdc.set(storageKey, Storage, meta)
    }

    override fun onDisable(player: Player, identifiers: Identifiers, holder: ProvidedHolder) {
        val meta = player.pdc.get(storageKey, Storage) ?: mutableMapOf()

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
        player.pdc.set(storageKey, Storage, meta)

        player.removePotionEffect(toRemove)
    }

    object Storage : PersistentDataType<PersistentDataContainer, MutableMap<UUID, Pair<PotionEffectType, Int>>> {
        override fun getPrimitiveType() = PersistentDataContainer::class.java

        override fun getComplexType(): Class<MutableMap<UUID, Pair<PotionEffectType, Int>>> {
            return MutableMap::class.java as Class<MutableMap<UUID, Pair<PotionEffectType, Int>>>
        }
        private val namespace = plugin.name.lowercase()

        override fun fromPrimitive(
            primitive: PersistentDataContainer,
            context: PersistentDataAdapterContext
        ): MutableMap<UUID, Pair<PotionEffectType, Int>> {
            val map = mutableMapOf<UUID, Pair<PotionEffectType, Int>>()
            for (key in primitive.keys) {
                if (key.namespace != namespace) continue
                val uuid = UUID.fromString(key.key)
                val pair = primitive.getString(key) ?: continue
                val index = pair.lastIndexOf(';')
                val type = PotionEffectType.getByName(pair.substring(0 until index)) ?: continue
                val power = pair.substring(index + 1).toInt()
                map[uuid] = type to power
            }
            return map
        }

        override fun toPrimitive(
            complex: MutableMap<UUID, Pair<PotionEffectType, Int>>,
            context: PersistentDataAdapterContext
        ): PersistentDataContainer {
            val new = context.newPersistentDataContainer()
            for ((id, pair) in complex) {
                new.setString(NamespacedKey(plugin, id.toString()), "${pair.first};${pair.second}")
            }
            return new
        }

    }
}
