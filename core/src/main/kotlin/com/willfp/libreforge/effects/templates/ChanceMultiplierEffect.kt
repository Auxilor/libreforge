package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.eco.util.randDouble
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.IdentifiedModifier
import com.willfp.libreforge.triggers.Dispatcher
import com.willfp.libreforge.triggers.PlayerDispatcher
import com.willfp.libreforge.triggers.get
import org.bukkit.entity.Player
import java.util.UUID

abstract class ChanceMultiplierEffect(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("chance", "You must specify the chance!")
    }

    private val modifiers = listMap<UUID, IdentifiedModifier>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        modifiers[dispatcher.uuid] += IdentifiedModifier(identifiers.uuid) {
            config.getDoubleFromExpression("chance", dispatcher.get<Player>()!!)
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[dispatcher.uuid].removeIf { it.uuid == identifiers.uuid }
    }

    protected fun passesChance(dispatcher: Dispatcher<*>): Boolean {
        var chance = 1.0

        for (modifier in modifiers[dispatcher.uuid]) {
            chance *= (100 - modifier.modifier) / 100
        }

        return randDouble(0.0, 1.0) > chance
    }

    @Deprecated(
        "Use passesChance(dispatcher: Dispatcher<*>) instead.",
        ReplaceWith("passesChance(dispatcher)"),
        DeprecationLevel.ERROR
    )
    protected fun passesChance(player: Player): Boolean =
        passesChance(PlayerDispatcher(player))
}
