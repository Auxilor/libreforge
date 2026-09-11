package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.invalidateSkinCache
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.skinApplier
import com.willfp.libreforge.integrations.skinsrestorer.skinsRestorer
import com.willfp.libreforge.plugin
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.skinsrestorer.api.property.SkinIdentifier
import org.bukkit.entity.Player

object EffectSrCopySkin : Effect<NoCompileData>("sr_copy_skin") {
    override val description = "Copies the victim's skin onto the player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Does nothing if the victim isn't a player."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.VICTIM
    )

    override val arguments = arguments {
        optional(
            "persist",
            description = "Whether the skin should be saved, so it stays applied when the player rejoins.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val victim = data.victim as? Player ?: return false
        val api = skinsRestorer ?: return false

        if (player.uniqueId == victim.uniqueId) {
            return false
        }

        val persist = config.getBoolOrNull("persist") ?: true

        plugin.scheduler.runAsync {
            val property = victim.lookupSkin().property
                ?: runCatching { api.playerStorage.getSkinOfPlayer(victim.uniqueId).orElse(null) }.getOrNull()
                ?: return@runAsync

            if (persist) {
                runCatching {
                    api.playerStorage.setSkinIdOfPlayer(player.uniqueId, SkinIdentifier.ofPlayer(victim.uniqueId))
                }
            }

            runCatching { skinApplier?.applySkin(player, property) }

            invalidateSkinCache(player.uniqueId)
        }

        return true
    }
}
