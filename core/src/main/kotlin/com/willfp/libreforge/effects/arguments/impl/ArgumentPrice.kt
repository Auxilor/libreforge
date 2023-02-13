package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.core.price.Prices
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.LibreforgeConfig
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.toMathContext
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Sound

object ArgumentPrice : EffectArgument<NoCompileData>("price") {

    /*

    I'll need to update prices to support copying with new base contexts.

    In the meantime, it will have to compile each time. Very annoying.

     */

    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toMathContext(trigger.data)
        )

        return price.canAfford(trigger.player)
    }

    override fun ifMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toMathContext(trigger.data)
        )

        price.pay(trigger.player)
    }

    override fun ifNotMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val price = Prices.create(
            element.config.getString("price.value"),
            element.config.getString("price.type"),
            element.config.toMathContext(trigger.data)
        )

        price.pay(trigger.player)

        val display = element.config.getString("price.display")
            .replace("%value%", NumberUtils.format(price.getValue(trigger.player)))

        val message = LibreforgeConfig.getMessage("cannot-afford-price")
            .replace("%price%", display)

        if (LibreforgeConfig.getBool("cannot-afford-price.in-actionbar")) {
            PlayerUtils.getAudience(trigger.player).sendActionBar(StringUtils.toComponent(message))
        } else {
            trigger.player.sendMessage(message)
        }

        if (LibreforgeConfig.getBool("cannot-afford-price.sound.enabled")) {
            trigger.player.playSound(
                trigger.player.location,
                Sound.valueOf(LibreforgeConfig.getString("cannot-afford-price.sound.sound").uppercase()),
                1.0f,
                LibreforgeConfig.getDouble("cannot-afford-price.sound.pitch").toFloat()
            )
        }
    }
}
