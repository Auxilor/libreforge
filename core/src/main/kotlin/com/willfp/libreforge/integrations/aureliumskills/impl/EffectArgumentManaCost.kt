package com.willfp.libreforge.integrations.aureliumskills.impl

import com.archyx.aureliumskills.api.AureliumAPI
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.EffectArgument
import com.willfp.libreforge.effects.ElementLike
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.toFriendlyPointName
import com.willfp.libreforge.triggers.DispatchedTrigger
import com.willfp.libreforge.triggers.InvocationData
import org.bukkit.Sound

object EffectArgumentManaCost : EffectArgument<NoCompileData>("mana_cost") {
    override fun isMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.data.player

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        return AureliumAPI.getMana(player) >= cost
    }

    override fun ifMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        AureliumAPI.setMana(trigger.player, AureliumAPI.getMana(trigger.player) - cost)
    }

    override fun ifNotMet(element: ElementLike, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.player

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        val message = plugin.langYml.getMessage("cannot-afford-type")
            .replace("%cost%", NumberUtils.format(cost))
            .replace("%type%", "mana".toFriendlyPointName())

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
}
