package com.willfp.libreforge.integrations.floodgate

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.placeholder.translatePlaceholders
import com.willfp.libreforge.integrations.floodgate.impl.TriggerBedrockFormClosed
import com.willfp.libreforge.integrations.floodgate.impl.TriggerBedrockFormResponse
import com.willfp.libreforge.plugin
import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.toPlaceholderContext
import com.willfp.libreforge.triggers.TriggerData
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.geysermc.floodgate.api.FloodgateApi
import org.geysermc.floodgate.api.player.FloodgatePlayer

/**
 * The Floodgate API.
 *
 * Only safe to touch from code that runs after the Floodgate integration has loaded,
 * which means everything registered by [FloodgateIntegration].
 */
internal val floodgate: FloodgateApi
    get() = FloodgateApi.getInstance()

/**
 * The Floodgate player for an entity, or null if it isn't a player, isn't a Bedrock
 * player, or Floodgate has no data for it.
 */
internal fun bedrockPlayerOf(entity: Entity?): FloodgatePlayer? {
    val player = entity as? Player ?: return null

    return floodgate.getPlayer(player.uniqueId)
}

/**
 * If an entity is a player connected through Bedrock edition.
 *
 * This is true both for unlinked Bedrock players and for those who have linked a Java
 * account and are therefore online under that account's UUID.
 */
internal fun isBedrockPlayer(entity: Entity?): Boolean {
    val player = entity as? Player ?: return false

    return floodgate.isFloodgatePlayer(player.uniqueId)
}

/**
 * Normalise a name for lenient matching, so that "KEYBOARD_MOUSE", "keyboard mouse",
 * and "KeyboardMouse" are all the same thing to a config author.
 */
private fun String.normalizeForMatching() = this
    .lowercase()
    .replace(" ", "")
    .replace("_", "")
    .replace("-", "")

/**
 * If this collection of config values names the given enum constant.
 *
 * Both the constant name and its display name are accepted, as Floodgate's enums have
 * friendlier toString values than their constant names (GOOGLE is "Android", NX is
 * "Switch"), and requiring the constant name would be a trap.
 *
 * Returns false for a null value, so that a filter or condition asking about a device
 * fails for players there is no Bedrock information for.
 */
internal fun Collection<String>.namesEnum(value: Enum<*>?): Boolean {
    value ?: return false

    val candidates = setOf(
        value.name.normalizeForMatching(),
        value.toString().normalizeForMatching()
    )

    return this.any { it.normalizeForMatching() in candidates }
}

/**
 * Run [commands] as the console for [player].
 *
 * Must already be on the main thread; see [handleFormResult], which is what gets it there.
 */
private fun runCommandsForPlayer(
    commands: Collection<String>,
    player: Player,
    config: Config,
    data: TriggerData
) {
    if (commands.isEmpty()) {
        return
    }

    val context = config.toPlaceholderContext(data)

    for (command in commands) {
        val parsed = command
            .replace("%player%", player.name)
            .translatePlaceholders(context)

        if (parsed.isBlank()) {
            continue
        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed)
    }
}

/**
 * Handle a player finishing with a form, whether they answered it or closed it.
 *
 * Runs the commands configured against whatever they picked, fires a [BedrockFormEvent], and
 * dispatches the matching trigger, in that order, so that an effect chain hanging off the
 * trigger sees the world the commands left behind.
 *
 * Cumulus calls response handlers on the netty thread, so everything here is bounced onto the
 * main thread first: commands, placeholders, and event handlers all assume they are on it.
 *
 * @param event The event describing what the player did.
 * @param commands The commands configured for what they picked, if any.
 * @param config The config of the effect that sent the form.
 * @param data The trigger data the form was sent from.
 */
internal fun handleFormResult(
    event: BedrockFormEvent,
    commands: Collection<String>,
    config: Config,
    data: TriggerData
) {
    plugin.scheduler.run {
        val player = event.player

        if (!player.isOnline) {
            return@run
        }

        runCommandsForPlayer(commands, player, config, data)

        Bukkit.getPluginManager().callEvent(event)

        val trigger = if (event.isClosed) TriggerBedrockFormClosed else TriggerBedrockFormResponse

        trigger.dispatch(
            player.toDispatcher(),
            TriggerData(
                player = player,
                location = player.location,
                event = event,
                text = event.buttonText ?: event.inputs.firstOrNull(),
                value = event.buttonId.toDouble()
            )
        )
    }
}
