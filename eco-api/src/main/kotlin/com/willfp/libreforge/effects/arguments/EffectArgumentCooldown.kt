package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.triggers.InvocationData
import org.bukkit.Sound
import java.util.UUID
import kotlin.math.ceil

object EffectArgumentCooldown : EffectArgument {
    // Maps ConfiguredEffect UUIDs to Player UUIDs mapped to expiry time
    private val cooldownTracker = mutableMapOf<UUID, MutableMap<UUID, Long>>()

    private val plugin = LibReforgePlugin.instance

    override fun isPresent(config: Config): Boolean = config.has("cooldown")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        return getCooldown(effect, data) <= 0
    }

    private fun getCooldown(effect: ConfiguredEffect, data: InvocationData): Int {
        val effectEndTimes = cooldownTracker[effect.uuid] ?: return 0
        val endTime = effectEndTimes[data.player.uniqueId] ?: return 0

        val msLeft = endTime - System.currentTimeMillis()
        val secondsLeft = ceil(msLeft.toDouble() / 1000).toLong()
        return secondsLeft.toInt()
    }

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val effectEndTimes = cooldownTracker[effect.uuid] ?: mutableMapOf()
        effectEndTimes[data.player.uniqueId] =
            System.currentTimeMillis() + (config.getDoubleFromExpression("cooldown", data.data) * 1000L).toLong()
        cooldownTracker[effect.uuid] = effectEndTimes
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        if (config.getBoolOrNull("send_cooldown_message") == false) {
            return
        }

        val player = data.player

        val cooldown = getCooldown(effect, data)

        val message = config.getStringOrNull("cooldown_message")
            ?.replace("%seconds%", cooldown.toString())
            ?.formatEco(data.player, true) ?: plugin.langYml.getMessage("on-cooldown")
            .replace("%seconds%", cooldown.toString())

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
}
