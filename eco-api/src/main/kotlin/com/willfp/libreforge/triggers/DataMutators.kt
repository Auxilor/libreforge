package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.triggers.mutators.MutatorLocationToBlock
import com.willfp.libreforge.triggers.mutators.MutatorLocationToPlayer
import com.willfp.libreforge.triggers.mutators.MutatorLocationToVictim
import com.willfp.libreforge.triggers.mutators.MutatorPlayerAsVictim
import com.willfp.libreforge.triggers.mutators.MutatorSpinLocation
import com.willfp.libreforge.triggers.mutators.MutatorTranslateLocation
import com.willfp.libreforge.triggers.mutators.MutatorVictimToOwner

@Suppress("UNUSED")
object DataMutators {
    private val BY_ID = mutableMapOf<String, DataMutator>()

    val TRANSLATE_LOCATION: DataMutator = MutatorTranslateLocation()
    val SPIN_LOCATION: DataMutator = MutatorSpinLocation()
    val PLAYER_AS_VICTIM: DataMutator = MutatorPlayerAsVictim()
    val VICTIM_TO_OWNER: DataMutator = MutatorVictimToOwner()
    val LOCATION_TO_PLAYER: DataMutator = MutatorLocationToPlayer()
    val LOCATION_TO_VICTIM: DataMutator = MutatorLocationToVictim()
    val LOCATION_TO_BLOCK: DataMutator = MutatorLocationToBlock()

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

    fun compile(config: Config, context: String): ConfiguredDataMutator? {
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
