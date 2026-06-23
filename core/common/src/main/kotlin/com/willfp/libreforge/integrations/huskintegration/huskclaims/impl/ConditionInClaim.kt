package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.huskclaims.api.BukkitHuskClaimsAPI
import org.bukkit.entity.Player

object ConditionInClaim : Condition<NoCompileData>("in_claim") {
    override val description = "Passes when the player is standing inside any HuskClaims claim."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the HuskClaims plugin.")

    override fun isMet(dispatcher: Dispatcher<*>, config: Config, holder: ProvidedHolder, compileData: NoCompileData): Boolean {
        dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false
        val position = BukkitHuskClaimsAPI.getInstance().getPosition(location)
        return BukkitHuskClaimsAPI.getInstance().getClaimAt(position).isPresent
    }
}
