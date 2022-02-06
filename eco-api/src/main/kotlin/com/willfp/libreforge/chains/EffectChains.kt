package com.willfp.libreforge.chains

import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.effects.Effects

@Suppress("UNUSED")
object EffectChains {
    private val BY_ID = HashBiMap.create<String, EffectChain>()

    /**
     * Get chain matching id.
     *
     * @param id The id to query.
     * @return The matching chain, or null if not found.
     */
    fun getByID(id: String): EffectChain? {
        return BY_ID[id]
    }

    /**
     * List of all registered chains.
     *
     * @return The chains.
     */
    fun values(): List<EffectChain> {
        return ImmutableList.copyOf(BY_ID.values)
    }

    /**
     * Compile an effect chain.
     *
     * @param config The config for the effect chain.
     * @param context The context to log violations for.
     *
     * @return The effect chain, or null if invalid.
     */
    @JvmStatic
    fun compile(config: Config, context: String): EffectChain? {
        val id = config.getString("id")

        val effects = config.getSubsections("effects").mapNotNull {
            Effects.compile(it, "$context (Chain ID $id) Effect", fromChain = true)
        }

        if (effects.isEmpty()) {
            return null
        }

        val chain = EffectChain(id, effects)

        BY_ID[id] = chain

        return chain
    }
}
