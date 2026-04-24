package com.willfp.libreforge.effects.impl

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigWarning
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.ViolationContext
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.get
import com.willfp.libreforge.getProvider
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

object EffectSetUnbreakable : Effect<NoCompileData>("set_unbreakable") {
    private val VALID_SLOTS = setOf("holder", "mainhand", "offhand", "helmet", "chestplate", "leggings", "boots")

    private data class EnabledState(val slot: String, val persistOnDisable: Boolean)
    private val enabledStates = HashMap<UUID, EnabledState>()

    override fun makeCompileData(config: Config, context: ViolationContext): NoCompileData {
        val slot = config.getStringOrNull("slot")
        if (slot != null && slot.lowercase() !in VALID_SLOTS) {
            context.log(
                this,
                ConfigWarning("slot", "Unknown slot '$slot'. Valid values: ${VALID_SLOTS.joinToString()}")
            )
        }
        return NoCompileData
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        val value = config.getBoolOrNull("value") ?: true
        val slot = config.getStringOrNull("slot") ?: "holder"
        val persistOnDisable = config.getBoolOrNull("persist_on_disable") ?: true

        enabledStates[identifiers.uuid] = EnabledState(slot, persistOnDisable)

        val player = dispatcher.get<Player>()
        val item = resolveItem(slot, player, holder) ?: return
        applyUnbreakable(item, value)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val state = enabledStates.remove(identifiers.uuid) ?: return
        if (state.persistOnDisable) return

        val player = dispatcher.get<Player>()
        val item = resolveItem(state.slot, player, holder) ?: return
        applyUnbreakable(item, false)
    }

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val value = config.getBoolOrNull("value") ?: true
        val slot = config.getStringOrNull("slot") ?: "holder"

        val player = (data.victim as? Player) ?: data.player
        val item = resolveItem(slot, player, data.holder) ?: return false
        return applyUnbreakable(item, value)
    }

    private fun resolveItem(slot: String, player: Player?, holder: ProvidedHolder): ItemStack? {
        val item = when (slot.lowercase()) {
            "holder" -> holder.getProvider<ItemStack>()
            "mainhand" -> player?.equipment?.itemInMainHand
            "offhand" -> player?.equipment?.itemInOffHand
            "helmet" -> player?.equipment?.helmet
            "chestplate" -> player?.equipment?.chestplate
            "leggings" -> player?.equipment?.leggings
            "boots" -> player?.equipment?.boots
            else -> null
        }
        return item?.takeIf { it.type != Material.AIR }
    }

    private fun applyUnbreakable(item: ItemStack, value: Boolean): Boolean {
        val meta = item.itemMeta ?: return false
        meta.isUnbreakable = value
        item.itemMeta = meta
        return true
    }
}