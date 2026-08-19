package com.willfp.libreforge.mutators

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.registry.Registry
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.mutators.impl.MutatorBlockAsDispatcher
import com.willfp.libreforge.mutators.impl.MutatorBlockToLocation
import com.willfp.libreforge.mutators.impl.MutatorBlockToRelative
import com.willfp.libreforge.mutators.impl.MutatorCenterLocation
import com.willfp.libreforge.mutators.impl.MutatorClampAltValue
import com.willfp.libreforge.mutators.impl.MutatorClampValue
import com.willfp.libreforge.mutators.impl.MutatorDispatcherAsBlock
import com.willfp.libreforge.mutators.impl.MutatorDispatcherAsPlayer
import com.willfp.libreforge.mutators.impl.MutatorDispatcherAsVictim
import com.willfp.libreforge.mutators.impl.MutatorItemToOffhand
import com.willfp.libreforge.mutators.impl.MutatorItemToSlot
import com.willfp.libreforge.mutators.impl.MutatorItemToVictimHand
import com.willfp.libreforge.mutators.impl.MutatorLocationAsDispatcher
import com.willfp.libreforge.mutators.impl.MutatorLocationLookAt
import com.willfp.libreforge.mutators.impl.MutatorLocationToBlock
import com.willfp.libreforge.mutators.impl.MutatorLocationToCursor
import com.willfp.libreforge.mutators.impl.MutatorLocationToDispatcher
import com.willfp.libreforge.mutators.impl.MutatorLocationToDrop
import com.willfp.libreforge.mutators.impl.MutatorLocationToEyes
import com.willfp.libreforge.mutators.impl.MutatorLocationToGround
import com.willfp.libreforge.mutators.impl.MutatorLocationToHighestBlock
import com.willfp.libreforge.mutators.impl.MutatorLocationToPlayer
import com.willfp.libreforge.mutators.impl.MutatorLocationToProjectile
import com.willfp.libreforge.mutators.impl.MutatorLocationToVictim
import com.willfp.libreforge.mutators.impl.MutatorLocationToWorldSpawn
import com.willfp.libreforge.mutators.impl.MutatorMoveLocation
import com.willfp.libreforge.mutators.impl.MutatorMultiplyVelocity
import com.willfp.libreforge.mutators.impl.MutatorPlayerAsDispatcher
import com.willfp.libreforge.mutators.impl.MutatorPlayerAsVictim
import com.willfp.libreforge.mutators.impl.MutatorProjectileShooterAsPlayer
import com.willfp.libreforge.mutators.impl.MutatorProjectileShooterAsVictim
import com.willfp.libreforge.mutators.impl.MutatorRandomiseLocation
import com.willfp.libreforge.mutators.impl.MutatorRoundLocation
import com.willfp.libreforge.mutators.impl.MutatorRoundAltValue
import com.willfp.libreforge.mutators.impl.MutatorRoundValue
import com.willfp.libreforge.mutators.impl.MutatorSetAltValue
import com.willfp.libreforge.mutators.impl.MutatorSetText
import com.willfp.libreforge.mutators.impl.MutatorSetValue
import com.willfp.libreforge.mutators.impl.MutatorSetVelocity
import com.willfp.libreforge.mutators.impl.MutatorSpinLocation
import com.willfp.libreforge.mutators.impl.MutatorSpinVelocity
import com.willfp.libreforge.mutators.impl.MutatorSwapPlayerAndVictim
import com.willfp.libreforge.mutators.impl.MutatorSwapValues
import com.willfp.libreforge.mutators.impl.MutatorTextCase
import com.willfp.libreforge.mutators.impl.MutatorTextToBlockType
import com.willfp.libreforge.mutators.impl.MutatorTextToPlayerName
import com.willfp.libreforge.mutators.impl.MutatorTextToVictimName
import com.willfp.libreforge.mutators.impl.MutatorTextToWorldName
import com.willfp.libreforge.mutators.impl.MutatorTranslateLocation
import com.willfp.libreforge.mutators.impl.MutatorValueToDistance
import com.willfp.libreforge.mutators.impl.MutatorVelocityToDirection
import com.willfp.libreforge.mutators.impl.MutatorVelocityToProjectile
import com.willfp.libreforge.mutators.impl.MutatorVelocityTowardsLocation
import com.willfp.libreforge.mutators.impl.MutatorVictimAsDispatcher
import com.willfp.libreforge.mutators.impl.MutatorVictimAsPlayer
import com.willfp.libreforge.mutators.impl.MutatorVictimToNearestEntity
import com.willfp.libreforge.mutators.impl.MutatorVictimToOwner
import com.willfp.libreforge.mutators.impl.MutatorVictimToPassenger
import com.willfp.libreforge.mutators.impl.MutatorVictimToVehicle

object Mutators: Registry<Mutator<*>>() {
    /**
     * Compile a list of [configs] into a MutatorList in a given [context].
     */
    fun compile(configs: Collection<Config>, context: ViolationContext): MutatorList =
        MutatorList(configs.mapNotNull { compile(it, context) })

    /**
     * Compile a [config] into a MutatorBlock in a given [context].
     */
    fun compile(config: Config, context: ViolationContext): MutatorBlock<*>? {
        val mutatorID = config.getString("id")
        val mutator = get(mutatorID)

        if (mutator == null) {
            context.log(ConfigViolation("id", "Invalid mutator ID specified: ${mutatorID}!"))
            return null
        }

        return makeBlock(mutator, config.getSubsection("args"), context.with("args"))
    }

    private fun <T> makeBlock(
        mutator: Mutator<T>,
        config: Config,
        context: ViolationContext
    ): MutatorBlock<T>? {
        if (!mutator.checkConfig(config, context)) {
            return null
        }

        val compileData = mutator.makeCompileData(config, context)

        return MutatorBlock(
            mutator,
            config,
            compileData,
        )
    }

    init {
        register(MutatorBlockToLocation)
        register(MutatorLocationToBlock)
        register(MutatorLocationToCursor)
        register(MutatorLocationToPlayer)
        register(MutatorLocationToProjectile)
        register(MutatorLocationToVictim)
        register(MutatorPlayerAsVictim)
        register(MutatorSpinLocation)
        register(MutatorSpinVelocity)
        register(MutatorTranslateLocation)
        register(MutatorVictimAsPlayer)
        register(MutatorVictimToOwner)
        register(MutatorVictimAsDispatcher)
        register(MutatorDispatcherAsPlayer)
        register(MutatorDispatcherAsVictim)
        register(MutatorLocationToDrop)
        register(MutatorLocationToDispatcher)
        register(MutatorLocationToEyes)
        register(MutatorLocationToWorldSpawn)
        register(MutatorLocationToHighestBlock)
        register(MutatorLocationToGround)
        register(MutatorLocationLookAt)
        register(MutatorCenterLocation)
        register(MutatorRoundLocation)
        register(MutatorMoveLocation)
        register(MutatorRandomiseLocation)
        register(MutatorBlockToRelative)
        register(MutatorDispatcherAsBlock)
        register(MutatorPlayerAsDispatcher)
        register(MutatorBlockAsDispatcher)
        register(MutatorLocationAsDispatcher)
        register(MutatorProjectileShooterAsPlayer)
        register(MutatorProjectileShooterAsVictim)
        register(MutatorSwapPlayerAndVictim)
        register(MutatorVictimToNearestEntity)
        register(MutatorVictimToVehicle)
        register(MutatorVictimToPassenger)
        register(MutatorItemToOffhand)
        register(MutatorItemToVictimHand)
        register(MutatorItemToSlot)
        register(MutatorSetText)
        register(MutatorTextToPlayerName)
        register(MutatorTextToVictimName)
        register(MutatorTextToBlockType)
        register(MutatorTextToWorldName)
        register(MutatorTextCase)
        register(MutatorSetValue)
        register(MutatorSetAltValue)
        register(MutatorSwapValues)
        register(MutatorValueToDistance)
        register(MutatorClampValue)
        register(MutatorClampAltValue)
        register(MutatorRoundValue)
        register(MutatorRoundAltValue)
        register(MutatorSetVelocity)
        register(MutatorMultiplyVelocity)
        register(MutatorVelocityToDirection)
        register(MutatorVelocityToProjectile)
        register(MutatorVelocityTowardsLocation)
    }
}
