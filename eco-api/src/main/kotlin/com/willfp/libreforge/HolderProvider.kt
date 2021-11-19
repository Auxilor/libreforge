package com.willfp.libreforge

import org.bukkit.entity.Player

interface HolderProvider {
    fun providerHolders(player: Player): Iterable<Holder>
}
