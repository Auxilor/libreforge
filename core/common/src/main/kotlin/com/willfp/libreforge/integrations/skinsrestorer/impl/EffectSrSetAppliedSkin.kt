package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.formatEco
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.integrations.skinsrestorer.currentSkinApplyEvent
import com.willfp.libreforge.integrations.skinsrestorer.parseSkinVariant
import com.willfp.libreforge.integrations.skinsrestorer.skinVariantChoices
import com.willfp.libreforge.integrations.skinsrestorer.skinsRestorer
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

object EffectSrSetAppliedSkin : Effect<NoCompileData>("sr_set_applied_skin") {
    override val description = "Replaces the skin that SkinsRestorer is about to apply with a different one."

    override val categories = setOf("player", "meta")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "Only works on the sr_apply_skin trigger, and only without a delay.",
        "The replacement skin must already be saved, as it cannot be fetched without blocking the apply. " +
            "Use sr_set_skin if the skin may not exist yet."
    )

    override val supportsDelay = false

    override val parameters = setOf(
        TriggerParameter.PLAYER
    )

    override val arguments = arguments {
        require(
            "skin",
            "You must specify the skin!",
            description = "The saved skin to apply instead: a player name, a skin URL, or a custom skin name. " +
                "Supports placeholders.",
            type = ArgType.STRING
        )
        optional(
            "variant",
            description = "The skin model variant to look up when the skin is a URL.",
            type = ArgType.STRING,
            default = "CLASSIC",
            choices = skinVariantChoices
        )
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val event = currentSkinApplyEvent.get() ?: return false
        val api = skinsRestorer ?: return false

        val skin = config.getString("skin").formatEco(config.toPlaceholderContext(data))

        if (skin.isEmpty()) {
            return false
        }

        val variant = parseSkinVariant(config.getStringOrNull("variant"))

        val result = runCatching { api.skinStorage.findSkinData(skin, variant) }
            .getOrNull()
            ?.orElse(null) ?: return false

        event.property = result.property

        return true
    }
}
