package com.willfp.libreforge.integrations.paper.impl

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import io.papermc.paper.event.block.TargetHitEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler

object TriggerTargetBlockHit : Trigger("target_block_hit") {
    override val parameters = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.BLOCK,
        TriggerParameter.LOCATION,
        TriggerParameter.VALUE,
        TriggerParameter.PROJECTILE
    )

    @EventHandler(ignoreCancelled = true)
    fun handle(event: TargetHitEvent) {
        val projectile = event.hitEntity as? Projectile ?: return
        val shooter = projectile.shooter as? LivingEntity ?: return

        this.dispatch(
            shooter.toDispatcher(),
            TriggerData(
                player = shooter as? Player,
                block = event.block,
                location = event.block.location,
                projectile = projectile,
                value = event.signalStrength.toDouble()
            )
        )
    }
}
