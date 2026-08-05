package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.husktowns.api.BukkitHuskTownsAPI
import org.bukkit.entity.Player

object ConditionInAnyClaim : Condition<NoCompileData>("in_any_claim") {
    override val description = "Passes when the player is standing inside any HuskTowns claim."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the HuskTowns plugin.")

    override fun isMet(dispatcher: Dispatcher<*>, config: Config, holder: ProvidedHolder, compileData: NoCompileData): Boolean {
        dispatcher.get<Player>() ?: return false
        val location = dispatcher.location ?: return false
        return BukkitHuskTownsAPI.getInstance().getClaimAt(location).isPresent
    }
}
