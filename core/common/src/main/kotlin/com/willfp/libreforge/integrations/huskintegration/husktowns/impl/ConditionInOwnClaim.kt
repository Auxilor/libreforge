package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.husktowns.api.BukkitHuskTownsAPI
import org.bukkit.entity.Player

object ConditionInOwnClaim : Condition<NoCompileData>("in_own_claim") {
    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false
        val position = BukkitHuskTownsAPI.getInstance().getPosition(location)

        val claimAtLocation = BukkitHuskTownsAPI.getInstance().getClaimAt(position)
        if (!claimAtLocation.isPresent) {
            return false
        }

        val playerTown = BukkitHuskTownsAPI.getInstance().getUserTown(player)
        if (!playerTown.isPresent) {
            return false
        }

        return claimAtLocation.get().town == playerTown.get().town
    }
}