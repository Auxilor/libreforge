package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.Compilable
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter

abstract class Mutator<T>(
    override val id: String
) : Compilable<T>() {
    /**
     * The run order.
     */
    open val runOrder = RunOrder.NORMAL

    /**
     * The parameter transformers.
     */
    protected open val parameterTransformers: Set<TriggerParameterTransformer> = emptySet()

    /**
     * Transform the parameters.
     */
    fun transform(parameters: Set<TriggerParameter>): Set<TriggerParameter> {
        return parameterTransformers.fold(parameters) { acc, transformer ->
            transformer.transform(acc)
        }
    }

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
        compileData: T
    ): TriggerData
}
