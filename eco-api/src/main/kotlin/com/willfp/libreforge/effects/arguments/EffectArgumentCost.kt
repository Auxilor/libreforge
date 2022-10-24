package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.toMathContext
import com.willfp.libreforge.triggers.InvocationData
import org.bukkit.Sound

object EffectArgumentCost : EffectArgument {
    private val plugin = LibReforgePlugin.instance

    override fun isPresent(config: Config): Boolean =
        config.has("cost")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        val player = data.player
        return Prices.lookup("cost", config.toMathContext(data.data)).canAfford(player)
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val player = data.player

        val cost = Prices.lookup("cost", config.toMathContext(data.data))

        val message = plugin.langYml.getMessage("cannot-afford").replace("%cost%", cost.displayText)

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

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        Prices.lookup("cost", config.toMathContext(data.data)).pay(data.player)
    }
}
