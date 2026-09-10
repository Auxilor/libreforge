package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.invalidateSkinCache
import com.willfp.libreforge.integrations.skinsrestorer.parseSkinVariant
import com.willfp.libreforge.integrations.skinsrestorer.skinApplier
import com.willfp.libreforge.integrations.skinsrestorer.skinVariantChoices
import com.willfp.libreforge.integrations.skinsrestorer.skinsRestorer
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSrSetSkin : Effect<NoCompileData>("sr_set_skin") {
    override val description = "Sets the player's skin to a player name, a skin URL, or a saved custom skin."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "The skin is resolved asynchronously, so it isn't applied on the same tick as the trigger."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "skin",
            "You must specify the skin!",
            description = "The skin to apply: a player name, a skin URL, or the name of a saved custom skin. " +
                "Supports placeholders.",
            type = ArgType.STRING,
            example = "Notch"
        )
        optional(
            "variant",
            description = "The skin model variant to use when the skin is generated from a URL.",
            type = ArgType.STRING,
            default = "CLASSIC",
            choices = skinVariantChoices
        )
        optional(
            "persist",
            description = "Whether the skin should be saved, so it stays applied when the player rejoins.",
            type = ArgType.BOOLEAN,
            default = "true"
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val api = skinsRestorer ?: return false

        val skin = config.getString("skin").formatEco(config.toPlaceholderContext(data))

        if (skin.isEmpty()) {
            return false
        }

        val variant = parseSkinVariant(config.getStringOrNull("variant"))
        val persist = config.getBoolOrNull("persist") ?: true

        plugin.scheduler.runAsync {
            val result = runCatching { api.skinStorage.findOrCreateSkinData(skin, variant) }
                .getOrNull()
                ?.orElse(null) ?: return@runAsync

            if (persist) {
                runCatching { api.playerStorage.setSkinIdOfPlayer(player.uniqueId, result.identifier) }
            }

            runCatching { skinApplier?.applySkin(player, result.property) }

            invalidateSkinCache(player.uniqueId)
        }

        return true
    }
}
