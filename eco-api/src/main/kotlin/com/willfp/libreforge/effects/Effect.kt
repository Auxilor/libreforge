package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.UUID
import kotlin.math.ceil

abstract class Effect(
    id: String,
    val supportsFilters: Boolean = false,
    val applicableTriggers: Collection<Trigger> = emptyList()
) : ConfigurableProperty(id), Listener {
    private val cooldownTracker = mutableMapOf<UUID, Long>()

    init {
        postInit()
    }

    private fun postInit() {
        Effects.addNewEffect(this)
    }

    fun getCooldown(player: Player): Int {
        val endTime = cooldownTracker[player.uniqueId] ?: return 0
        val msLeft = endTime - System.currentTimeMillis()
        val secondsLeft = ceil(msLeft.toDouble() / 1000).toLong()
        return secondsLeft.toInt()
    }

    fun sendCooldownMessage(player: Player) {
        val cooldown = getCooldown(player)

        val message = plugin.langYml.getMessage("on-cooldown").replace("%seconds%", cooldown.toString())
        if (plugin.configYml.getBool("cooldown.in-actionbar")) {
            PlayerUtils.getAudience(player).sendActionBar(StringUtils.toComponent(message))
        } else {
            player.sendMessage(message)
        }

        if (plugin.configYml.getBool("cooldown.sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("cooldown.sound.sound").uppercase()),
                1.0f,
                plugin.configYml.getDouble("cooldown.sound.pitch").toFloat()
            )
        }
    }

    fun resetCooldown(player: Player, config: Config) {
        if (!config.has("cooldown")) {
            return
        }

        cooldownTracker[player.uniqueId] = System.currentTimeMillis() + (config.getDouble("cooldown") * 1000L).toLong()
    }

    /**
     * Generate a UUID with a specified offset.
     *
     * @param offset The offset.
     * @return The UUID.
     */
    fun getUUID(
        offset: Int
    ): UUID {
        return UUID.nameUUIDFromBytes("$id$offset".toByteArray())
    }

    /**
     * Generate a NamespacedKey with a specified offset.
     *
     * @param offset The offset.
     * @return The NamespacedKey.
     */
    fun getNamespacedKey(
        offset: Int
    ): NamespacedKey {
        return this.plugin.namespacedKeyFactory.create("${id}_$offset")
    }

    /**
     * Handle application of this effect.
     *
     * @param player The player.
     * @param config The config.
     */
    fun enableForPlayer(
        player: Player,
        config: Config
    ) {
        player.pushEffect(this)
        handleEnable(player, config)
    }

    protected open fun handleEnable(
        player: Player,
        config: Config
    ) {
        // Override when needed.
    }

    /**
     * Handle removal of this effect.
     *
     * @param player The player.
     */
    fun disableForPlayer(player: Player) {
        handleDisable(player)
        player.popEffect(this)
    }

    protected open fun handleDisable(player: Player) {
        // Override when needed.
    }

    open fun handle(data: TriggerData, config: Config) {
        // Override when needed
    }
}

data class ConfiguredEffect(
    val effect: Effect,
    val args: Config,
    val filter: Filter,
    val triggers: Collection<Trigger>
)