package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.parseSkinVariant
import com.willfp.libreforge.integrations.skinsrestorer.skinVariantChoices
import com.willfp.libreforge.integrations.skinsrestorer.variant
import org.bukkit.entity.Player

object ConditionSrSkinVariantIs : Condition<NoCompileData>("sr_skin_variant_is") {
    override val description = "Passes when the player's skin uses the given model variant."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "CLASSIC is the four pixel wide arm model, SLIM is the three pixel wide one."
    )

    override val arguments = arguments {
        require(
            "variant",
            "You must specify the skin variant!",
            description = "The skin model variant to check for.",
            type = ArgType.STRING,
            choices = skinVariantChoices
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val lookup = player.lookupSkin()

        val variant = lookup.identifier?.skinVariant
            ?: lookup.property?.variant()
            ?: return false

        return variant == parseSkinVariant(config.getString("variant"))
    }
}
