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

object EffectSrUpdateSkinData : Effect<NoCompileData>("sr_update_skin_data") {
    override val description = "Fetches the player's skin from Mojang again, then re-applies it."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Makes a request to Mojang, so it should not be run on a frequently firing trigger."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = skinsRestorer ?: return false

        plugin.scheduler.runAsync {
            val property = runCatching { api.skinStorage.updatePlayerSkinData(player.uniqueId) }
                .getOrNull()
                ?.orElse(null)

            invalidateSkinCache(player.uniqueId)

            runCatching {
                if (property == null) {
                    skinApplier?.applySkin(player)
                } else {
                    skinApplier?.applySkin(player, property)
                }
            }
        }

        return true
    }
}
