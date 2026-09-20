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
import net.skinsrestorer.api.property.SkinIdentifier

object EffectSrGenerateSkin : Effect<NoCompileData>("sr_generate_skin") {
    override val description = "Generates a skin from an image URL through MineSkin, then applies it to the player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "MineSkin is heavily rate limited, so this should only be run on rare triggers. " +
            "Prefer sr_set_skin with a URL, which reuses skins that have already been generated."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "url",
            "You must specify the image URL!",
            description = "The URL of the skin image to generate from. Supports placeholders.",
            type = ArgType.STRING
        )
        optional(
            "variant",
            description = "The skin model variant to generate.",
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

        val url = config.getString("url").formatEco(config.toPlaceholderContext(data))

        if (url.isEmpty()) {
            return false
        }

        val variant = parseSkinVariant(config.getStringOrNull("variant"))
        val persist = config.getBoolOrNull("persist") ?: true

        plugin.scheduler.runAsync {
            val response = runCatching { api.mineSkinAPI.genSkin(url, variant) }.getOrNull() ?: return@runAsync

            runCatching { api.skinStorage.setURLSkinByResponse(url, response) }

            if (persist) {
                runCatching {
                    api.playerStorage.setSkinIdOfPlayer(
                        player.uniqueId,
                        SkinIdentifier.ofURL(url, response.generatedVariant)
                    )
                }
            }

            runCatching { skinApplier?.applySkin(player, response.property) }

            invalidateSkinCache(player.uniqueId)
        }

        return true
    }
}
