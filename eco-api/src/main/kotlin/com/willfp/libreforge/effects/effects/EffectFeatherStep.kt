package com.willfp.libreforge.effects.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import com.willfp.libreforge.triggers.Triggers
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EffectFeatherStep : Effect("feather_step") {

    private val players = mutableListOf<Player>()

    override fun handleEnable(player: Player, config: Config) {
        players.add(player)
    }

    override fun handleDisable(player: Player) {
        players.remove(player)
    }

    @EventHandler(ignoreCancelled = true)
    fun handle(event: PlayerInteractEvent) {
        if (event.action != Action.PHYSICAL) return
        if (players.contains(event.player)) {
            event.isCancelled = true
        }
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        return mutableListOf()
    }
}
