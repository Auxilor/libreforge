package com.willfp.libreforge.triggers

import org.bukkit.entity.Player

data class DispatchedTrigger(
    val player: Player,
    val trigger: Trigger,
    val data: TriggerData
)
