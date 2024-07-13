package com.willfp.libreforge.effects

import com.willfp.libreforge.proxy.Proxy

@Proxy("effects.VersionSpecificEffectsImpl")
interface VersionSpecificEffects {
    fun loadEffects()
}
