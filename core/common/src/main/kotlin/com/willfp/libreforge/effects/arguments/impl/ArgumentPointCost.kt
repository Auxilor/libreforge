package com.willfp.libreforge.effects.arguments.impl

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.get
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.points
import com.willfp.libreforge.toFriendlyPointName
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.Sound
import org.bukkit.entity.Player

object ArgumentPointCost : EffectArgument<NoCompileData>("point_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val cost = element.config.getDoubleFromExpression("point_cost.cost", trigger.data)
        val type = element.config.getString("point_cost.type")

        return player.points[type] >= cost
    }

    override fun ifNotMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        if (!plugin.configYml.getBool("cannot-afford-type.message-enabled")) {
            return
        }

        val cost = element.config.getDoubleFromExpression("point_cost.cost", trigger.data)
        val type = element.config.getString("point_cost.type")

        val message = plugin.langYml.getFormattedString("messages.cannot-afford-type")
            .replace("%cost%", NumberUtils.format(cost))
            .replace("%type%", type.toFriendlyPointName())

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

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getDoubleFromExpression("point_cost.cost", trigger.data)
        val type = element.config.getString("point_cost.type")

        player.points[type] -= cost
    }
}
