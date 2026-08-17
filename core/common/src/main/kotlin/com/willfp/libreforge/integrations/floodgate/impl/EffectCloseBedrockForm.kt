package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectCloseBedrockForm : Effect<NoCompileData>("close_bedrock_form") {
    override val description = "Closes whatever Bedrock form the player currently has open."

    override val categories = setOf("chat", "player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does nothing for Java players, and does nothing if no form is open."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (!isBedrockPlayer(player)) {
            return false
        }

        return floodgate.closeForm(player.uniqueId)
    }
}
