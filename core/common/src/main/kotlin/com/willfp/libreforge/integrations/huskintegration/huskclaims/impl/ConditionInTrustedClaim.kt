package com.willfp.libreforge.integrations.huskintegration.huskclaims.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.huskclaims.api.BukkitHuskClaimsAPI
import org.bukkit.entity.Player

object ConditionInTrustedClaim : Condition<NoCompileData>("in_trusted_claim") {
    override val description = "Passes when the player is standing inside a HuskClaims claim that they own or are trusted in."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the HuskClaims plugin.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false
        val api = BukkitHuskClaimsAPI.getInstance()
        val position = api.getPosition(location)

        val claimOptional = api.getClaimAt(position)
        if (!claimOptional.isPresent) return false

        val claim = claimOptional.get()
        val isOwner = claim.owner.map { it == player.uniqueId }.orElse(false)
        return isOwner || claim.trustedUsers.containsKey(player.uniqueId)
    }
}
