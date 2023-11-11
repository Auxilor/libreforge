package com.willfp.libreforge.effects.impl.bossbar

import net.kyori.adventure.bossbar.BossBar
import java.util.UUID

data class RegistrableBossBar(
    val id: String,
    val bossBar: BossBar,
    val uuid: UUID
)
