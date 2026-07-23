package com.willfp.libreforge.integrations.custom_blocks.craftengine

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.integrations.LoadableIntegration

/**
 * CraftEngine has no drop interception yet - unlike ItemsAdder, Nexo and
 * Oraxen, nothing routes its custom block drops through the drop pipeline, so
 * telekinesis and the drop effects don't see them.
 *
 * The CraftEngine-specific telekinesis effect that used to live here didn't do
 * it either: both of its event handlers had empty bodies, and registering it
 * under the "telekinesis" id replaced the generic effect for every drop on the
 * server.
 */
object CraftEngineIntegration : LoadableIntegration {
    override fun load(plugin: EcoPlugin) {
        // Nothing to register yet.
    }

    override fun getPluginName(): String {
        return "CraftEngine"
    }
}
