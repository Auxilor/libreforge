package com.willfp.libreforge.integrations.lands.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.plugin
import me.angeschossen.lands.api.LandsIntegration
import org.bukkit.entity.Player

object ConditionHasLandsRole : Condition<NoCompileData>("has_lands_role") {
    override val description = "Passes when the player holds one of the specified roles in the Lands area at their current location."
    override val categories = setOf("world", "player")
    override val additionalInfo = listOf("Requires the Lands plugin.")

    override val arguments = arguments {
        require(
            "roles",
            "You must specify the roles!",
            description = "A list of Lands role names to check against (e.g. owner, member).",
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
        val location = dispatcher.location ?: return false
        val area = LandsIntegration.of(plugin).getArea(location) ?: return false
        val roleName = area.getRole(player.uniqueId)?.name ?: return false

        return config.getStrings("roles").any {
            it.equals(roleName, ignoreCase = true)
        }
    }
}