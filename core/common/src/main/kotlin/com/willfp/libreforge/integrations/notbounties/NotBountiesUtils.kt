package com.willfp.libreforge.integrations.notbounties

import me.jadenp.notbounties.utils.BountyManager
import org.bukkit.entity.Player

internal val Player.totalBounty: Double
    get() = BountyManager.getBounty(this.uniqueId)?.totalDisplayBounty ?: 0.0
