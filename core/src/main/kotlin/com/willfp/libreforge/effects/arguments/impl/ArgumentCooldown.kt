package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NamedValue
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Chain
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.ifType
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.ceil

object ArgumentCooldown : EffectArgument<Chain?>("cooldown") {
    // Maps ConfiguredEffect UUIDs to Player UUIDs mapped to expiry time
    private val cooldownTracker = mutableMapOf<UUID, MutableMap<UUID, Long>>()

    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: Chain?): Boolean {
        return getCooldown(element, trigger) <= 0
    }

    private fun getCooldown(element: ConfigurableElement, trigger: DispatchedTrigger): Int {
        val effectEndTimes = cooldownTracker[element.uuid] ?: return 0
        val endTime = effectEndTimes[trigger.dispatcher.uuid] ?: return 0

        val msLeft = endTime - System.currentTimeMillis()
        val secondsLeft = ceil(msLeft.toDouble() / 1000).toLong()
        return secondsLeft.toInt()
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: Chain?) {
        val effectEndTimes = cooldownTracker[element.uuid] ?: mutableMapOf()
        effectEndTimes[trigger.dispatcher.uuid] = System.currentTimeMillis() +
                (element.config.getDoubleFromExpression("cooldown", trigger.data) * 1000L).toLong()
        cooldownTracker[element.uuid] = effectEndTimes
    }

    override fun ifNotMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: Chain?) {
        val cooldown = getCooldown(element, trigger)

        compileData?.trigger(trigger.copy().apply {
            addPlaceholder(NamedValue("seconds", cooldown))
        })

        if (element.config.getBoolOrNull("send_cooldown_message") == false) {
            return
        }

        if (!plugin.configYml.getBool("cannot-afford-type.message-enabled")) {
            return
        }

        trigger.dispatcher.ifType<Player> { player ->
            val message = element.config.getStringOrNull("cooldown_message")
                ?.replace("%seconds%", cooldown.toString())
                ?.formatEco(player, true)

                ?: plugin.langYml.getFormattedString("messages.on-cooldown")
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

    override fun makeCompileData(config: Config, context: ViolationContext): Chain? {
        return Effects.compileChain(
            config.getSubsections("cooldown_effects"),
            context.with("cooldown_effects")
        )
    }
}
