package com.willfp.libreforge.integrations.customcrops.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent

object FilterCustomFishType : Filter<NoCompileData, Collection<String>>("custom_fish_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as FishingLootSpawnEvent
        val loot = event.loot.id() ?: return true

        return value.contains(loot)
    }
}