package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*
import kotlin.math.ceil

@Suppress("UNUSED_PARAMETER")
abstract class Effect(
    id: String,
    private val triggers: () -> Collection<Trigger> = { emptyList() },
    supportsFilters: Boolean = true,
    noDelay: Boolean = false
) : ConfigurableProperty(id), Listener {
    private val cooldownTracker = mutableMapOf<UUID, MutableMap<UUID, Long>>()

    val applicableTriggers: Collection<Trigger>
        get() = triggers()

    val supportsFilters = applicableTriggers.isNotEmpty()
    val noDelay: Boolean

    init {
        if (applicableTriggers.isEmpty()) {
            this.noDelay = true
        } else {
            this.noDelay = noDelay
        }

        postInit()
    }

    private fun postInit() {
        Effects.addNewEffect(this)
    }

    fun getCooldown(player: Player, uuid: UUID): Int {
        val endTime = (cooldownTracker[player.uniqueId] ?: return 0)[uuid] ?: return 0
        val msLeft = endTime - System.currentTimeMillis()
        val secondsLeft = ceil(msLeft.toDouble() / 1000).toLong()
        return secondsLeft.toInt()
    }

    fun sendCooldownMessage(player: Player, uuid: UUID) {
        val cooldown = getCooldown(player, uuid)

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

    fun sendCannotAffordMessage(player: Player, cost: Double) {
        val message = plugin.langYml.getMessage("cannot-afford").replace("%cost%", cost.toString())
        if (plugin.configYml.getBool("cannot-afford.in-actionbar")) {
            PlayerUtils.getAudience(player).sendActionBar(StringUtils.toComponent(message))
        } else {
            player.sendMessage(message)
        }

        if (plugin.configYml.getBool("cannot-afford.sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("cannot-afford.sound.sound").uppercase()),
                1.0f,
                plugin.configYml.getDouble("cannot-afford.sound.pitch").toFloat()
            )
        }
    }

    fun sendCannotAffordTypeMessage(player: Player, cost: Double, type: String) {
        val message = plugin.langYml.getMessage("cannot-afford-type").replace("%cost%", cost.toString())
            .replace("%type%", type)
        if (plugin.configYml.getBool("cannot-afford-type.in-actionbar")) {
            PlayerUtils.getAudience(player).sendActionBar(StringUtils.toComponent(message))
        } else {
            player.sendMessage(message)
        }

        if (plugin.configYml.getBool("cannot-afford-type.sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("cannot-afford-type.sound.sound").uppercase()),
                1.0f,
                plugin.configYml.getDouble("cannot-afford-type.sound.pitch").toFloat()
            )
        }
    }

    fun resetCooldown(player: Player, config: Config, uuid: UUID) {
        if (!config.has("cooldown")) {
            return
        }
        val current = cooldownTracker[player.uniqueId] ?: mutableMapOf()
        current[uuid] =
            System.currentTimeMillis() + (config.getDoubleFromExpression("cooldown", player) * 1000L).toLong()
        cooldownTracker[player.uniqueId] = current
    }

    /**
     * Enable this effect for a player (for permanent effects).
     *
     * @param player The player.
     * @param config The config.
     * @param identifiers The identifiers (keys / UUIDs).
     */
    open fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        // Override when needed.
    }

    /**
     * Disable this effect for a player (for permanent effects).
     *
     * @param player The player.
     * @param identifiers The identifiers (keys / UUIDs).
     */
    open fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        // Override when needed.
    }

    open fun handle(data: TriggerData, config: Config) {
        // Override when needed
    }

    open fun handle(invocation: InvocationData, config: Config) {
        // Override when needed
    }

    open fun makeCompileData(config: Config, context: String): CompileData? {
        return null
    }
}

data class MultiplierModifier(val uuid: UUID, val multiplier: Double)