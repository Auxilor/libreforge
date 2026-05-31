package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.entity.Player

object ConditionIsFlying : Condition<NoCompileData>("is_flying") {
    override val description = "Passes when the player is in creative or spectator flight mode."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires a player dispatcher — elytra gliding is not considered flying by this condition."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.isFlying
    }
}
