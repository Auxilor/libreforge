package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.invalidateSkinCache
import com.willfp.libreforge.integrations.skinsrestorer.skinApplier
import com.willfp.libreforge.integrations.skinsrestorer.skinsRestorer
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSrClearSkin : Effect<NoCompileData>("sr_clear_skin") {
    override val description = "Removes the player's saved skin, restoring their default skin."

    override val categories = setOf("player")

    override val additionalInfo = listOf("Requires SkinsRestorer to be installed.")

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = skinsRestorer ?: return false

        plugin.scheduler.runAsync {
            runCatching { api.playerStorage.removeSkinIdOfPlayer(player.uniqueId) }

            invalidateSkinCache(player.uniqueId)

            runCatching { skinApplier?.applySkin(player) }
        }

        return true
    }
}
