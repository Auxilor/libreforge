package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.husktowns.api.BukkitHuskTownsAPI
import org.bukkit.entity.Player

object ConditionHasTown : Condition<NoCompileData>("has_town") {
    override val description = "Passes when the player is a member of a HuskTowns town."
    override val categories = setOf("player")
    override val additionalInfo = listOf("Requires the HuskTowns plugin.")

    override fun isMet(dispatcher: Dispatcher<*>, config: Config, holder: ProvidedHolder, compileData: NoCompileData): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        return BukkitHuskTownsAPI.getInstance().getUserTown(player).isPresent
    }
}
