package com.willfp.libreforge.integrations

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.Integration

interface LoadableIntegration : Integration {
    /**
     * Load the integration.
     */
    fun load(plugin: EcoPlugin)
}
