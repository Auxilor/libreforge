package com.willfp.libreforge.proxy.modern.effects

import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.effects.VersionSpecificEffects
import com.willfp.libreforge.proxy.modern.effects.impl.EffectBlockReach
import com.willfp.libreforge.proxy.modern.effects.impl.EffectEntityReach
import com.willfp.libreforge.proxy.modern.effects.impl.EffectGravityMultiplier
import com.willfp.libreforge.proxy.modern.effects.impl.EffectIncreaseStepHeight
import com.willfp.libreforge.proxy.modern.effects.impl.EffectJumpStrengthMultiplier
import com.willfp.libreforge.proxy.modern.effects.impl.EffectMiningEfficiency
import com.willfp.libreforge.proxy.modern.effects.impl.EffectMiningSpeedMultiplier
import com.willfp.libreforge.proxy.modern.effects.impl.EffectMovementEfficiencyMultiplier
import com.willfp.libreforge.proxy.modern.effects.impl.EffectSafeFallDistance
import com.willfp.libreforge.proxy.modern.effects.impl.EffectSneakingSpeedMultiplier
import com.willfp.libreforge.proxy.modern.effects.impl.EffectUnderwaterMiningSpeedMultiplier

class VersionSpecificEffectsImpl: VersionSpecificEffects {
    override fun loadEffects() {
        with(Effects) {
            register(EffectBlockReach)
            register(EffectMiningEfficiency)
            register(EffectEntityReach)
            register(EffectGravityMultiplier)
            register(EffectIncreaseStepHeight)
            register(EffectJumpStrengthMultiplier)
            register(EffectMiningSpeedMultiplier)
            register(EffectMovementEfficiencyMultiplier)
            register(EffectSneakingSpeedMultiplier)
            register(EffectUnderwaterMiningSpeedMultiplier)
            register(EffectSafeFallDistance)
        }
    }
}
