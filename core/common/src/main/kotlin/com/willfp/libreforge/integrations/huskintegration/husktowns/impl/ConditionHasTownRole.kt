package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.husktowns.api.BukkitHuskTownsAPI
import org.bukkit.entity.Player

object ConditionHasTownRole : Condition<NoCompileData>("has_town_role") {
    override val arguments = arguments {
        require("roles", "You must specify the roles!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false
        val role = BukkitHuskTownsAPI.getInstance().getUserTown(player).get().role.name ?: return false

        return config.getStrings("roles")
            .containsIgnoreCase(role)
    }
}