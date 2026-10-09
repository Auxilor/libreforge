package com.willfp.libreforge.integrations.smartpets

import com.smartpets.api.SmartPetsAPI
import com.smartpets.events.SmartPetEvent
import com.smartpets.model.Pet
import com.willfp.libreforge.Regions
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.updateEffects
import org.bukkit.Bukkit
import org.bukkit.entity.Player

internal val smartPets: SmartPetsAPI?
    get() = SmartPetsAPI.getInstance()

internal val Player.activePet: Pet?
    get() = smartPets?.getActivePet(this)

internal val SmartPetEvent.owner: Player?
    get() = player ?: ownerId?.let { Bukkit.getPlayer(it) }

/**
 * SmartPets fires its events off the main thread when the change happens async.
 */
internal fun Player.onMainThread(action: () -> Unit) {
    if (Regions.owns(this)) {
        action()
    } else {
        plugin.scheduler.on(this).run { action() }
    }
}

internal fun Trigger.dispatchPetEvent(event: SmartPetEvent, value: Double, text: String?) {
    val player = event.owner ?: return

    player.onMainThread {
        this.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                event = event,
                location = player.location,
                value = value,
                text = text
            )
        )
    }
}

internal fun SmartPetEvent.updateOwnerEffects() {
    val player = owner ?: return

    player.onMainThread {
        player.toDispatcher().updateEffects()
    }
}
