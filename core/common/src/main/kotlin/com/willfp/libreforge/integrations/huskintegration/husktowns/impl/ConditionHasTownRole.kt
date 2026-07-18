package com.willfp.libreforge.integrations.huskintegration.husktowns.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import net.william278.husktowns.api.BukkitHuskTownsAPI
import org.bukkit.entity.Player

object ConditionHasTownRole : Condition<NoCompileData>("has_town_role") {
    override val description = "Passes when the player holds one of the specified roles in their HuskTowns town."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the HuskTowns plugin.")

    override val arguments = arguments {
        require(
            "roles",
            "You must specify the roles!",
            description = "A list of HuskTowns town role names to check against (e.g. mayor, resident).",
            type = ArgType.STRING_LIST
        )
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