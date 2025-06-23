package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.huskclaims.api.BukkitHuskClaimsAPI
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
        val position = BukkitHuskClaimsAPI.getInstance().getPosition(location)

        val claimOwner = BukkitHuskClaimsAPI.getInstance().getClaimOwnerAt(position)
        if (!claimOwner.isPresent) {
            return false
        }

        return claimOwner.get().uuid == player.uniqueId
    }
}