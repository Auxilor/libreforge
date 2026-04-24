package com.willfp.libreforge.integrations.mythicmobs.utils

import io.lumine.mythic.bukkit.MythicBukkit
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

fun Entity.isMythicMob(): Boolean {
    if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
        if (MythicBukkit.inst().mobManager.isMythicMob(this)) {
            return true
        }
    }
    return false
}
