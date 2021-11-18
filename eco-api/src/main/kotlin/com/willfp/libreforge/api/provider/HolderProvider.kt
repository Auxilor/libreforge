package com.willfp.libreforge.api.provider

import org.bukkit.entity.Player

interface HolderProvider {
    fun providerHolders(player: Player): Iterable<Holder>
}