package com.willfp.libreforge.integrations.notbounties.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import me.jadenp.notbounties.utils.BountyManager
import org.bukkit.entity.Player

object ConditionNbHasBounty : Condition<NoCompileData>("nb_has_bounty") {
    override val description = "Passes when the player has a NotBounties bounty on them."
    override val categories = setOf("player")
    override val additionalInfo = listOf("Requires the NotBounties plugin.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return BountyManager.hasBounty(player.uniqueId)
    }
}
