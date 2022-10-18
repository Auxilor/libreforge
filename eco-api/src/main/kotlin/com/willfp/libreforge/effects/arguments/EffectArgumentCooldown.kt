package com.willfp.libreforge.effects.arguments

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.ConfiguredEffect
import com.willfp.libreforge.effects.GenericEffectArgument
import com.willfp.libreforge.triggers.InvocationData
import java.util.UUID

object EffectArgumentCooldown : GenericEffectArgument {
    override fun isPresent(config: Config): Boolean = true

    override fun isMet(effect: ConfiguredEffect, data: InvocationData, config: Config): Boolean {
        return effect.effect.getCooldown(data.player, effect.uuid) <= 0
    }

    override fun ifMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        effect.effect.resetCooldown(data.player, config, effect.uuid)
    }

    override fun ifNotMet(effect: ConfiguredEffect, data: InvocationData, config: Config) {
        if (config.getBoolOrNull("send_cooldown_message") == false) {
            return
        }

        effect.effect.sendCooldownMessage(data.player, effect.uuid)
    }
}
