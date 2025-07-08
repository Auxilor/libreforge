package com.willfp.libreforge.integrations.aureliumskills.impl

import com.archyx.aureliumskills.api.AureliumAPI
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.SoundUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.ConfigurableElement
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.get
import com.willfp.libreforge.getDoubleFromExpression
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toFriendlyPointName
import com.willfp.libreforge.triggers.DispatchedTrigger
import org.bukkit.entity.Player

@Deprecated("AureliumSkills integration is deprecated and will be removed in the future. Update to AuraSkills instead!")
object ArgumentManaCost : EffectArgument<NoCompileData>("mana_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        return AureliumAPI.getMana(player) >= cost
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        AureliumAPI.setMana(player, AureliumAPI.getMana(player) - cost)
    }

    override fun ifNotMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        if (!plugin.configYml.getBool("cannot-afford-type.message-enabled")) {
            return
        }

        val message = plugin.langYml.getFormattedString("messages.cannot-afford-type")
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
                SoundUtils.getSound(plugin.configYml.getString("cannot-afford-type.sound.sound"))!!,
                1.0f,
                plugin.configYml.getDouble("cannot-afford-type.sound.pitch").toFloat()
            )
        }
    }
}
