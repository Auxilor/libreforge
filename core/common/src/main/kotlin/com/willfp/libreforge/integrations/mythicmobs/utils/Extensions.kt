package com.willfp.libreforge.integrations.mythicmobs.utils

import com.willfp.eco.core.integrations.DisabledIntegrations
import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.entity.Entity

fun Entity.isMythicMob(): Boolean {
    if (DisabledIntegrations.isEnabled("MythicMobs")) {
        if (MythicBukkit.inst().mobManager.isMythicMob(this)) {
            return true
        }
    }
    return false
}
