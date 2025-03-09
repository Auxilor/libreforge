package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.tmmobcoins.lib.CBA.CBAMethods.name
import org.bukkit.Bukkit

object EffectBroadcast : Effect<NoCompileData>("broadcast") {
    override val isPermanent = false

    override val arguments = arguments {
        require(listOf("message", "messages"), "You must specify the message(s) to send!")
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val messages = config.getStrings("messages", "message")
            .map { it.replace("%player%", data.player?.name ?: "%player%") }
            .formatEco(config.toPlaceholderContext(data))
            .dropLastWhile { it.isEmpty() }

        for (message in messages) {
            @Suppress("DEPRECATION")
            Bukkit.getServer().broadcastMessage(message)
        }

        return true
    }
}
