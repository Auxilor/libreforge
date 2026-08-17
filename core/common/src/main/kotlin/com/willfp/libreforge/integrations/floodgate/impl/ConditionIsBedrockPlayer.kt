package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.floodgate.isBedrockPlayer
import org.bukkit.entity.Player

object ConditionIsBedrockPlayer : Condition<NoCompileData>("is_bedrock_player") {
    override val description = "Passes when the player is playing on Bedrock edition through Geyser."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Passes for linked Bedrock players too, even though they are online under their Java account's UUID.",
        "Set inverse: true, or use the java_join trigger, to target Java players instead."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return isBedrockPlayer(player)
    }
}
