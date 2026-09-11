package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.skinsrestorer.hasCape
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import org.bukkit.entity.Player

object ConditionSrHasCape : Condition<NoCompileData>("sr_has_cape") {
    override val description = "Passes when the player's skin includes a cape."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Only reads the cape stored on the SkinsRestorer skin, so it does not see capes applied by other plugins."
    )

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return player.lookupSkin().property?.hasCape() ?: false
    }
}
