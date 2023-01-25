package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.triggers.mutators.*

@Suppress("UNUSED")
object DataMutators {
    private val BY_ID = mutableMapOf<String, DataMutator>()

    val TRANSLATE_LOCATION: DataMutator = MutatorTranslateLocation
    val SPIN_LOCATION: DataMutator = MutatorSpinLocation
    val PLAYER_AS_VICTIM: DataMutator = MutatorPlayerAsVictim
    val VICTIM_TO_OWNER: DataMutator = MutatorVictimToOwner
    val LOCATION_TO_PLAYER: DataMutator = MutatorLocationToPlayer
    val LOCATION_TO_VICTIM: DataMutator = MutatorLocationToVictim
    val LOCATION_TO_BLOCK: DataMutator = MutatorLocationToBlock
    val SPIN_VELOCITY: DataMutator = MutatorSpinVelocity
    val VICTIM_AS_PLAYER: DataMutator = MutatorVictimAsPlayer
    val LOCATION_TO_PROJECTILE: DataMutator = MutatorLocationToProjectile
    val LOCATION_TO_CURSOR: DataMutator = MutatorLocationToCursor
    val BLOCK_TO_LOCATION: DataMutator = MutatorBlockToLocation

    fun values(): Set<DataMutator> {
        return BY_ID.values.toSet()
    }

    fun getById(id: String): DataMutator? {
        return BY_ID[id.lowercase()]
    }

    fun addNewMutator(mutator: DataMutator) {
        BY_ID.remove(mutator.id)
        BY_ID[mutator.id] = mutator
    }

    /**
     * Compile a group of data mutators.
     *
     * @param configs The mutator configs.
     * @param context The context to log violations for.
     * @return The compiled mutators.
     */
    @JvmStatic
    fun compile(
        configs: Iterable<Config>,
        context: ViolationContext
    ): List<ConfiguredDataMutator> = configs.mapNotNull { compile(it, context) }


    /**
     * Compile data mutator.
     *
     * @param cfg The config for the mutator.
     * @param context The context to log violations for.
     * @return The mutator, or null if invalid.
     */
    @JvmStatic
    fun compile(cfg: Config, context: ViolationContext): ConfiguredDataMutator? {
        val config = cfg.separatorAmbivalent()

        val mutator = config.getString("id").let {
            val found = getById(it)
            if (found == null) {
                LibReforgePlugin.instance.logViolation(
                    it,
                    context,
                    ConfigViolation("id", "Invalid mutator ID specified!")
                )
            }

            found
        } ?: return null

        val args = config.getSubsection("args")
        if (mutator.checkConfig(args, context)) {
            return null
        }

        return ConfiguredDataMutator(mutator, args)
    }
}
