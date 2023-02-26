package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Sound
import java.util.UUID
import kotlin.math.ceil

object ArgumentCooldown : EffectArgument<NoCompileData>("cooldown") {
    // Maps ConfiguredEffect UUIDs to Player UUIDs mapped to expiry time
    private val cooldownTracker = mutableMapOf<UUID, MutableMap<UUID, Long>>()

    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        return getCooldown(element, trigger) <= 0
    }

    private fun getCooldown(element: ElementLike, trigger: DispatchedTrigger): Int {
        val effectEndTimes = cooldownTracker[element.uuid] ?: return 0
        val endTime = effectEndTimes[trigger.player.uniqueId] ?: return 0

        val msLeft = endTime - System.currentTimeMillis()
        val secondsLeft = ceil(msLeft.toDouble() / 1000).toLong()
        return secondsLeft.toInt()
    }

    override fun ifMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val effectEndTimes = cooldownTracker[element.uuid] ?: mutableMapOf()
        effectEndTimes[trigger.player.uniqueId] = System.currentTimeMillis() +
                (element.config.getDoubleFromExpression("cooldown", trigger.data) * 1000L).toLong()
        cooldownTracker[element.uuid] = effectEndTimes
    }

    override fun ifNotMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        if (element.config.getBoolOrNull("send_cooldown_message") == false) {
            return
        }

        val player = trigger.player

        val cooldown = getCooldown(element, trigger)

        val message = element.config.getStringOrNull("cooldown_message")
            ?.replace("%seconds%", cooldown.toString())
            ?.formatEco(trigger.player, true)

            ?: plugin.langYml.getMessage("on-cooldown")
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
