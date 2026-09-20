package com.willfp.libreforge.integrations.skinsrestorer.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.libreforge.getStrings
import com.willfp.libreforge.integrations.skinsrestorer.lookupSkin
import com.willfp.libreforge.integrations.skinsrestorer.textureHash
import org.bukkit.entity.Player

object ConditionSrSkinTextureHashIs : Condition<NoCompileData>("sr_skin_texture_hash_is") {
    override val description = "Passes when the texture hash of the player's skin is one of the given hashes."

    override val categories = setOf("player")

    override val additionalInfo = listOf(
        "Requires SkinsRestorer to be installed.",
        "The texture hash is the last part of a Minecraft texture URL, and identifies the exact image."
    )

    override val arguments = arguments {
        require(
            listOf("hash", "hashes"),
            "You must specify the texture hash(es)!",
            description = "The texture hashes to check against.",
            type = ArgType.STRING_LIST
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        val hash = player.lookupSkin().property?.textureHash() ?: return false

        return config.getStrings("hashes", "hash").containsIgnoreCase(hash)
    }
}
