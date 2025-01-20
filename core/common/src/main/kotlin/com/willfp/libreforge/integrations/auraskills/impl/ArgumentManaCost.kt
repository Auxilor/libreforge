package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.PlayerUtils
import com.willfp.eco.util.SoundUtils
import com.willfp.eco.util.StringUtils
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.arguments.EffectArgument
import com.willfp.libreforge.triggers.DispatchedTrigger
import dev.aurelium.auraskills.api.AuraSkillsApi
import org.bukkit.Sound
import org.bukkit.entity.Player

object ArgumentManaCost : EffectArgument<NoCompileData>("mana_cost") {
    override fun isMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData): Boolean {
        val player = trigger.dispatcher.get<Player>() ?: return false

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        return AuraSkillsApi.get().getUser(player.uniqueId).mana >= cost
    }

    override fun ifMet(element: ConfigurableElement, trigger: DispatchedTrigger, compileData: NoCompileData) {
        val player = trigger.dispatcher.get<Player>() ?: return

        val cost = element.config.getDoubleFromExpression("mana_cost", trigger.data)

        val user = AuraSkillsApi.get().getUser(player.uniqueId)
        user.mana -= cost
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
