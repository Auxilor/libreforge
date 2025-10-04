package com.willfp.libreforge.conditions.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.eco.util.safeNamespacedKeyOf
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import org.bukkit.Registry
import org.bukkit.advancement.Advancement
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object ConditionHasCompletedAdvancement : Condition<NoCompileData>("has_completed_advancement") {
    override val arguments = arguments {
        require("advancement", "You must specify the advancement!")
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
