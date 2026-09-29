package com.willfp.libreforge.effects.templates

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.map.listMap
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.IdentifiedModifier
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import org.bukkit.entity.Player
import java.util.UUID

abstract class MultiplierEffect(id: String) : Effect<NoCompileData>(id) {
    override val arguments = arguments {
        require(
            "multiplier",
            "You must specify the multiplier!",
            description = "The multiplier to apply. Supports expressions.",
            type = ArgType.EXPRESSION
        )
    }

    private val modifiers = listMap<UUID, IdentifiedModifier>()

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        modifiers[dispatcher.uuid].add(IdentifiedModifier(identifiers.uuid) {
            config.getDoubleFromExpression("multiplier", dispatcher.get<Player>()!!)
        })
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val uuid = dispatcher.uuid

        if (!modifiers.containsKey(uuid)) {
            return
        }

        val dispatcherModifiers = modifiers[uuid]
        dispatcherModifiers.removeIf { it.uuid == identifiers.uuid }

        if (dispatcherModifiers.isEmpty()) {
            modifiers.remove(uuid)
        }
    }

    protected fun getMultiplier(dispatcher: Dispatcher<*>): Double {
        val uuid = dispatcher.uuid
        var multiplier = 1.0

        if (!modifiers.containsKey(uuid)) {
            return multiplier
        }

        for (modifier in modifiers[uuid]) {
            multiplier *= modifier.modifier
        }

        return multiplier
    }

}
