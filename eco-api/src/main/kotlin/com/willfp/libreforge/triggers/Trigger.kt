package com.willfp.libreforge.triggers

import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.util.NumberUtils
import com.willfp.libreforge.Holder
import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforge.events.EffectActivateEvent
import com.willfp.libreforge.getHolders
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.Objects
import java.util.UUID

private val everyHandler = mutableMapOf<UUID, MutableMap<UUID, Int>>()

abstract class Trigger(
    val id: String,
    val parameters: Collection<TriggerParameter>
) : Listener {
    protected val plugin = LibReforgePlugin.instance

    init {
        postInit()
    }

    private fun postInit() {
        Triggers.addNewTrigger(this)
    }

    protected fun processTrigger(player: Player, data: TriggerData, forceHolders: Iterable<Holder>? = null) {
        for (holder in forceHolders ?: player.getHolders()) {
            var areMet = true
            for ((condition, config) in holder.conditions) {
                if (!condition.isConditionMet(player, config)) {
                    areMet = false
                }
            }

            if (!areMet) {
                continue
            }

            for ((effect, config, filter, triggers, uuid, conditions) in holder.effects) {
                var effectAreMet = true
                for ((condition, conditionConfig) in conditions) {
                    if (!condition.isConditionMet(player, conditionConfig)) {
                        effectAreMet = false
                    }
                }

                if (!effectAreMet) {
                    continue
                }

                if (NumberUtils.randFloat(0.0, 100.0) > (config.getDoubleOrNull("chance") ?: 100.0)) {
                    continue
                }

                if (config.has("check_antigrief") && config.getBool("check_antigrief") && data.player != null && data.victim != null) {
                    if (!AntigriefManager.canInjure(data.player, data.victim)) {
                        continue
                    }
                }

                val every = config.getIntOrNull("every") ?: 0

                if (every > 0) {
                    val everyMap = everyHandler[player.uniqueId] ?: mutableMapOf()
                    var current = everyMap[uuid] ?: 0

                    if (current != 0) {
                        current++

                        if (current >= every) {
                            current = 0
                        }

                        everyHandler[player.uniqueId] = everyMap.apply {
                            this[uuid] = current
                        }

                        continue
                    }
                }

                if (!triggers.contains(this)) {
                    continue
                }

                if (!filter.matches(data)) {
                    continue
                }

                if (effect.getCooldown(player, uuid) > 0) {
                    if (config.getBoolOrNull("send_cooldown_message") != false) {
                        effect.sendCooldownMessage(player, uuid)
                    }
                    continue
                }

                if (config.has("cost")) {
                    if (!EconomyManager.hasAmount(player, config.getDoubleFromExpression("cost"))) {
                        effect.sendCannotAffordMessage(player, config.getDoubleFromExpression("cost"))
                        continue
                    }

                    EconomyManager.removeMoney(player, config.getDoubleFromExpression("cost"))
                }

                val activateEvent = EffectActivateEvent(player, holder, effect)
                this.plugin.server.pluginManager.callEvent(activateEvent)

                if (!activateEvent.isCancelled) {
                    effect.resetCooldown(player, config, uuid)

                    effect.handle(data, config)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Trigger) {
            return false
        }

        return this.id == other.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }
}
