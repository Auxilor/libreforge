package com.willfp.libreforge.filters.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.filters.Filter
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.entity.Boss
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.Warden

object FilterIsBoss : Filter<NoCompileData, Boolean>("is_boss") {
    override val description = "Matches when the victim is (or is not) a boss entity."
    override val categories = setOf("entity")
    override val valueType = ArgType.BOOLEAN
    override val additionalInfo = listOf("Passes automatically when no victim is present in the trigger data.")

    override fun getValue(config: Config, data: TriggerData?, key: String): Boolean {
        return config.getBool(key)
    }

    override fun isMet(data: TriggerData, value: Boolean, compileData: NoCompileData): Boolean {
        val entity = data.victim ?: return true
        return (entity is Boss || entity is ElderGuardian || entity is Warden) == value
    }
}
