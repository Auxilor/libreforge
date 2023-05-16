package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.integrations.economy.balance
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Sound

object ArgumentCost : EffectArgument<NoCompileData>("cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.player
        return player.balance >= element.config.getDoubleFromExpression("cost", trigger.data)
    }

    override fun ifNotMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        if (!plugin.configYml.getBool("cannot-afford.message-enabled")) {
            return
        }

        val player = trigger.player

        val cost = element.config.getDoubleFromExpression("cost", trigger.data)

        val message = plugin.langYml.getMessage("cannot-afford").replace("%cost%", NumberUtils.format(cost))

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

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        trigger.player.balance -= element.config.getDoubleFromExpression("cost", trigger.data)
    }
}
