package com.willfp.libreforge.integrations.auraskills.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.registry.NamespacedId
import dev.aurelium.auraskills.api.stat.StatModifier
import org.bukkit.entity.Player

object EffectMultiplyStat : Effect<NoCompileData>("multiply_stat") {
    override val arguments = arguments {
        require("stat", "You must specify the stat!")
        require("multiplier", "You must specify the multiplier!")
    }

    override val shouldReload = false

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
        val multiplier = config.getDoubleFromExpression("multiplier", player)

        val baseValue = user.getStatLevel(stat)
        val additionalAmount = baseValue * (multiplier - 1)

        user.addStatModifier(StatModifier(
            identifiers.key.key,
            stat,
            additionalAmount
        ))
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val player = dispatcher.get<Player>() ?: return

        val user = AuraSkillsApi.get().getUser(player.uniqueId)
        user.removeStatModifier(identifiers.key.key)
    }
}