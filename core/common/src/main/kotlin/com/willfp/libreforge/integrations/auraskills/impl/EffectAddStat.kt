package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.*
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.stat.StatModifier
import org.bukkit.entity.Player

object EffectAddStat : Effect<NoCompileData>("add_stat") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("amount", "You must specify the amount!")
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val player = dispatcher.get<Player>() ?: return

        val auraSkills = AuraSkillsApi.get()
        val user = auraSkills.getUser(player.uniqueId)
        val stat = auraSkills.globalRegistry.getStat(NamespacedId.fromDefault(config.getString("stat")))

        user.addStatModifier(
            StatModifier(
                identifiers.key.key,
                stat,
                config.getDoubleFromExpression("amount", player)
            )
        )
    }

    override fun onDisable(
        dispatcher: Dispatcher<*>,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        isReload: Boolean
    ) {
        if (isReload) return
        val player = dispatcher.get<Player>() ?: return
        val user = AuraSkillsApi.get().getUser(player.uniqueId)

        user.removeStatModifier(identifiers.key.key)
    }
}
