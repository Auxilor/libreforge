package com.willfp.libreforge.integrations.mythicmobs.impl.filter

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import io.lumine.mythic.bukkit.MythicBukkit
import io.lumine.mythic.core.mobs.ActiveMob

object FilterMythicMobType : Filter<NoCompileData, Collection<String>>("mythicmob") {

    override fun getValue(config: Config, data: TriggerData?, key: String): Collection<String> {
        return config.getStrings(key)
    }

    override fun isMet(data: TriggerData, value: Collection<String>, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true
        val mob: ActiveMob = MythicBukkit.inst().mobManager.getMythicMobInstance(entity) ?: return false
        val mobType = mob.type.internalName
        return value.any {
            it.equals(mobType, ignoreCase = true)
        }
    }

}
