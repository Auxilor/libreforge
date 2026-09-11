package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import org.bukkit.entity.Player

object ConditionSrHasSkin : Condition<NoCompileData>("sr_has_skin") {
    override val description = "Passes when the player has a skin set through SkinsRestorer."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires SkinsRestorer to be installed.")

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.lookupSkin().identifier != null
    }
}
