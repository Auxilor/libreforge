package com.willfp.libreforge.integrations.floodgate.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.floodgate.bedrockPlayerOf
import org.bukkit.entity.Player

object ConditionHasLinkedJavaAccount : Condition<NoCompileData>("has_linked_java_account") {
    override val description = "Passes when the player is on Bedrock edition and has linked a Java account."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires the Floodgate plugin.",
        "Never passes for players who are already on Java edition; it only describes Bedrock players.",
        "A linked player is online under their Java account's UUID and name, not a Floodgate one."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val bedrock = bedrockPlayerOf(player) ?: return false

        return bedrock.isLinked
    }
}
