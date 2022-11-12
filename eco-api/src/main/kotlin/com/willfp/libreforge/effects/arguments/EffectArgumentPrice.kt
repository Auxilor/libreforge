package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.toMathContext
import com.willfp.libreforge.triggers.InvocationData
import org.bukkit.Sound

object EffectArgumentPrice : EffectArgument {
    private val plugin = LibReforgePlugin.instance

    override fun isPresent(config: Config): Boolean =
        config.has("price")

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        val player = data.player

        val price = Prices.create(
            config.getString("price.value"),
            config.getString("price.type"),
            config.toMathContext(data.data)
        )

        return price.canAfford(player)
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val player = data.player

        val price = Prices.create(
            config.getString("price.value"),
            config.getString("price.type"),
            config.toMathContext(data.data)
        )

        val display = config.getString("price.display")
            .replace("%value%", NumberUtils.format(price.getValue(player)))

        val message = plugin.langYml.getMessage("cannot-afford-price")
            .replace("%price%", display)

        if (plugin.configYml.getBool("cannot-afford-price.in-actionbar")) {
            PlayerUtils.getAudience(player).sendActionBar(StringUtils.toComponent(message))
        } else {
            player.sendMessage(message)
        }

        if (plugin.configYml.getBool("cannot-afford-price.sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("cannot-afford-price.sound.sound").uppercase()),
                1.0f,
                plugin.configYml.getDouble("cannot-afford-price.sound.pitch").toFloat()
            )
        }
    }

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        val player = data.player

        val price = Prices.create(
            config.getString("price.value"),
            config.getString("price.type"),
            config.toMathContext(data.data)
        )

        price.pay(player)
    }
}
