package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.safeNamespacedKeyOf
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.Registry
import org.bukkit.entity.Player

object ConditionHasCompletedAdvancement : Condition<NoCompileData>("has_completed_advancement") {
    override val description = "Passes when the player has completed the specified advancement."
    override val categories = setOf("player")

    override val arguments = arguments {
        require(
            "advancement",
            "You must specify the advancement!",
            description = "The namespaced key of the advancement (e.g. minecraft:story/mine_stone).",
            type = ArgType.STRING,
            example = "minecraft:story/mine_stone"
        )
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        @Suppress("DEPRECATION", "REMOVAL")
        val advancement = Registry.ADVANCEMENT.get(
            safeNamespacedKeyOf(config.getString("advancement")) ?: return false
        ) ?: return false

        return player.getAdvancementProgress(advancement).isDone
    }
}
