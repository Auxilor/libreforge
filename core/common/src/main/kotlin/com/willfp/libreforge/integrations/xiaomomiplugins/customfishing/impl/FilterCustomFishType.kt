package com.willfp.libreforge.integrations.xiaomomiplugins.customfishing.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent
import org.bukkit.entity.Item

object FilterCustomFishType : Filter<NoCompileData, Collection<String>>("custom_fish_type") {
    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val event = data.event as FishingLootSpawnEvent
        val lootId = event.loot.id()
        val itemStack = (event.entity as? Item)?.itemStack?.type?.name

        return if (lootId == "vanilla") {
            // I am unsure how to tie this into the Item-Lookup-System.
            // Needs some amendments from Aux to improve this here.
            return value.any { it.equals(itemStack, ignoreCase = true) }
        } else {
            // This is fine, it is checking the CustomFishing loot ID, not an eco lookup.
            return value.any { it.equals(lootId, ignoreCase = true) }
        }
    }
}