package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Sound
import org.bukkit.entity.Player

object ArgumentPrice : EffectArgument<NoCompileData>("price") {

    /*

    I'll need to update prices to support copying with new base contexts.

    In the meantime, it will have to compile each time. Very annoying.

     */

    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toPlaceholderContext(trigger.data)
        )

        return price.canAfford(player)
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toPlaceholderContext(trigger.data)
        )

        price.pay(player)
    }

    override fun ifNotMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        if (!plugin.configYml.getBool("cannot-afford-price.message-enabled")) {
            return
        }

        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toPlaceholderContext(trigger.data)
        )

        val display = element.config.getString("price.display")
            .replace("%value%", NumberUtils.format(price.getValue(player)))

        val message = plugin.langYml.getFormattedString("messages.cannot-afford-price")
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
}
