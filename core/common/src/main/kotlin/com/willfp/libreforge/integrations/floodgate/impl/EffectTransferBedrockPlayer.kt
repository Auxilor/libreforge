package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.integrations.floodgate.floodgate
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectTransferBedrockPlayer : Effect<NoCompileData>("transfer_bedrock_player") {
    override val description = "Transfers the player to another Bedrock server, disconnecting them from this one."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Does nothing for Java players, as the transfer is a Bedrock protocol feature.",
        "The address and port must point at a Bedrock-reachable endpoint, which usually means a Geyser listener rather than the Java port.",
        "The player leaves this server entirely, so run this last in a chain."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "address",
            "You must specify the address to transfer to!",
            description = "The hostname or IP address of the server to transfer the player to.",
            type = ArgType.STRING,
            example = "play.example.com"
        )
        optional(
            "port",
            description = "The port of the server to transfer the player to.",
            type = ArgType.EXPRESSION,
            default = "19132",
            example = "19132"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        if (!isBedrockPlayer(player)) {
            return false
        }

        val address = config.getString("address")
            .translatePlaceholders(config.toPlaceholderContext(data))

        if (address.isBlank()) {
            return false
        }

        val port = if (config.has("port")) config.getIntFromExpression("port", data) else 19132

        return floodgate.transferPlayer(player.uniqueId, address, port)
    }
}
