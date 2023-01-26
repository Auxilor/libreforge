package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData

abstract class Mutator<T>(
    override val id: String
) : ConfigurableProperty() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * Mutate the trigger data.
     *
     * @param data The data.
     * @param config The config.
     * @return The modified data.
     */
    fun mutate(
        data: TriggerData,
        config: MutatorBlock<T>,
    ): TriggerData = mutate(data, config.config, config.compileData)

    /**
     * Mutate the trigger data.
     *
     * @param data The data.
     * @param config The config.
     * @param compileData The compile data.
     * @return The modified data.
     */
    protected abstract fun mutate(
        data: TriggerData,
        config: Config,
        compileData: T?
    ): TriggerData

    /**
     * @param config The config.
     * @param context The context to log violations for.
     * @return The compile data.
     */
    open fun makeCompileData(config: Config, context: ViolationContext): T? {
        return null
    }
}
