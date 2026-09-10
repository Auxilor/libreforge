package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.invalidateSkinCache
import com.willfp.libreforge.integrations.skinsrestorer.skinApplier
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import net.skinsrestorer.api.property.SkinProperty

object EffectSrSetSkinFromTexture : Effect<NoCompileData>("sr_set_skin_from_texture") {
    override val description = "Applies a raw skin texture, made up of a value and a signature, to the player."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "The skin is not saved, so it is lost when the player rejoins. Use sr_set_skin to save a skin."
    )

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "value",
            "You must specify the texture value!",
            description = "The base64 texture value of the skin. Supports placeholders.",
            type = ArgType.STRING
        )
        require(
            "signature",
            "You must specify the texture signature!",
            description = "The signature that goes with the texture value. Supports placeholders.",
            type = ArgType.STRING
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false

        val context = config.toPlaceholderContext(data)

        val value = config.getString("value").formatEco(context)
        val signature = config.getString("signature").formatEco(context)

        if (value.isEmpty() || signature.isEmpty()) {
            return false
        }

        val property = SkinProperty.of(value, signature)

        plugin.scheduler.runAsync {
            runCatching { skinApplier?.applySkin(player, property) }

            invalidateSkinCache(player.uniqueId)
        }

        return true
    }
}
