package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.effects.IdentifiedModifier
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.PlayerDispatcher
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import java.util.UUID

abstract class MultiplierEffect(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require("multiplier", "You must specify the multiplier!")
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
            config.getDoubleFromExpression("multiplier", dispatcher.get<Player>()!!)
        }
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        modifiers[dispatcher.uuid].removeIf { it.uuid == identifiers.uuid }
    }

    protected fun getMultiplier(dispatcher: Dispatcher<*>): Double {
        var multiplier = 1.0

        for (modifier in modifiers[dispatcher.uuid]) {
            multiplier *= modifier.modifier
        }

        return multiplier
    }

    @Deprecated(
        "Use getMultiplier(dispatcher: Dispatcher<*>) instead.",
        ReplaceWith("getMultiplier(dispatcher)"),
        DeprecationLevel.ERROR
    )
    protected fun getMultiplier(player: Player): Double =
        getMultiplier(PlayerDispatcher(player))
}
