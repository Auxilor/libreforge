package com.willfp.libreforge.provider

import org.bukkit.entity.Player

interface HolderProvider {
    fun providerHolders(player: Player): Iterable<Holder>
}