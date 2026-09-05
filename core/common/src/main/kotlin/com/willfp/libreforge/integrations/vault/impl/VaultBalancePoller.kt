package com.willfp.libreforge.integrations.vault.impl

import com.willfp.eco.core.scheduling.EcoTask
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.impl.TriggerGainCurrency
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object VaultBalancePoller : Listener {
    private val lastSeenBalances = ConcurrentHashMap<UUID, BigDecimal>()
    private var task: EcoTask? = null

    fun start() {
        val intervalTicks = plugin.configYml.getInt("triggers.gain-currency.vault-poll-interval").toLong()

        if (intervalTicks <= 0) {
            return
        }

        task = plugin.scheduler.runTimer(intervalTicks, intervalTicks) {
            poll()
        }
    }

    fun stop() {
        task?.cancel()
        task = null
    }

    private fun poll() {
        val registration = Bukkit.getServicesManager().getRegistration(Economy::class.java) ?: return
        val economy = registration.provider

        // EcoBits fires its own CurrencyGainEvent when it's the registered Vault provider,
        // so polling it here too would double-fire the gain_currency trigger.
        if (economy.javaClass.name.startsWith("com.willfp.ecobits.")) {
            return
        }

        for (player in Bukkit.getOnlinePlayers()) {
            val newBalance = economy.getBalance(player).toBigDecimal()
            val previousBalance = lastSeenBalances[player.uniqueId]

            lastSeenBalances[player.uniqueId] = newBalance

            if (previousBalance != null && newBalance > previousBalance) {
                TriggerGainCurrency.dispatch(
                    player.toDispatcher(),
                    TriggerData(
                        player = player,
                        value = (newBalance - previousBalance).toDouble(),
                        altValue = newBalance.toDouble(),
                        text = "vault"
                    )
                )
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        lastSeenBalances.remove(event.player.uniqueId)
    }
}
