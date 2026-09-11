package com.willfp.libreforge.integrations.luckperms.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getFormattedString
import com.willfp.libreforge.integrations.luckperms.LuckPermsManager
import com.willfp.libreforge.triggers.TriggerData

object EffectLpSendCustomMessage : Effect<NoCompileData>("lp_send_custom_message") {
    override val description = "Sends a custom message over the LuckPerms messaging service."
    override val categories = setOf("permission", "meta")
    override val additionalInfo = listOf(
        "Requires the LuckPerms plugin.",
        "Requires a LuckPerms messaging service to be configured, otherwise this does nothing.",
        "Pairs with the lp_custom_message trigger to run effects on other servers on the network.",
        "The message is not received by the server that sent it."
    )

    override val isPermanent = false

    override val arguments = arguments {
        require(
            "channel",
            "You must specify the channel!",
            description = "The channel to send the message on. Receiving servers filter on this.",
            type = ArgType.STRING,
            example = "myserver:rewards"
        )
        require(
            "payload",
            "You must specify the payload!",
            description = "The message payload, supporting placeholders.",
            type = ArgType.STRING,
            example = "%player_name% won the event"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val messagingService = LuckPermsManager.luckPerms?.messagingService?.orElse(null) ?: return false

        messagingService.sendCustomMessage(
            config.getString("channel"),
            config.getFormattedString("payload", data)
        )

        return true
    }
}
