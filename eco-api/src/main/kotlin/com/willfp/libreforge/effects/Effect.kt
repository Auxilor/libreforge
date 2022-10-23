package com.willfp.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.triggers.InvocationData
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Player
import org.bukkit.event.Listener

private val noTriggers: (Trigger) -> Boolean = { false }

@Suppress("UNUSED_PARAMETER")
abstract class Effect @JvmOverloads constructor(
    id: String,
    private val triggers: (Trigger) -> Boolean = noTriggers,
    supportsFilters: Boolean = true,
    noDelay: Boolean = false,
    val runOrder: RunOrder = RunOrder.NORMAL,
    val shouldRefresh: Boolean = false
) : ConfigurableProperty(id), Listener {
    val isPermanent = triggers === noTriggers // Identity equals thanks to default constructor parameter
    val noDelay: Boolean

    init {
        if (isPermanent) {
            this.noDelay = true
        } else {
            this.noDelay = noDelay
        }

        postInit()
    }

    private fun postInit() {
        Effects.addNewEffect(this)
    }

    fun supportsTrigger(trigger: Trigger) = triggers(trigger)

    /**
     * Enable this effect for a player (for permanent effects).
     *
     * @param player The player.
     * @param config The config.
     * @param identifiers The identifiers (keys / UUIDs).
     */
    open fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        // Override when needed.
    }

    /**
     * Enable this effect for a player (for permanent effects).
     *
     * @param player The player.
     * @param config The config.
     * @param identifiers The identifiers (keys / UUIDs).
     * @param compileData The compile data.
     */
    open fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers,
        compileData: CompileData?
    ) {
        // Override when needed.
    }

    /**
     * Disable this effect for a player (for permanent effects).
     *
     * @param player The player.
     * @param identifiers The identifiers (keys / UUIDs).
     */
    open fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        // Override when needed.
    }

    open fun handle(data: TriggerData, config: Config) {
        // Override when needed
    }

    open fun handle(invocation: InvocationData, config: Config) {
        // Override when needed
    }

    open fun makeCompileData(config: Config, context: String): CompileData? {
        return null
    }
}

