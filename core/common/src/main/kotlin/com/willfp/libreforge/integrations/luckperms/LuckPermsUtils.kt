package com.willfp.libreforge.integrations.luckperms

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.getIntFromExpression
import com.willfp.libreforge.triggers.TriggerData
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.cacheddata.CachedMetaData
import net.luckperms.api.cacheddata.CachedPermissionData
import net.luckperms.api.context.ImmutableContextSet
import net.luckperms.api.context.MutableContextSet
import net.luckperms.api.model.data.NodeMap
import net.luckperms.api.model.user.User
import net.luckperms.api.platform.PlayerAdapter
import net.luckperms.api.query.QueryOptions
import net.luckperms.api.track.Track
import org.bukkit.entity.Player
import java.time.Duration

/**
 * Holds the LuckPerms API instance and the player adapter, which are only
 * available once LuckPerms has enabled.
 */
internal object LuckPermsManager {
    private var api: LuckPerms? = null
    private var adapter: PlayerAdapter<Player>? = null

    val luckPerms: LuckPerms?
        get() = api ?: runCatching { LuckPermsProvider.get() }.getOrNull()?.also { api = it }

    val playerAdapter: PlayerAdapter<Player>?
        get() = adapter ?: luckPerms?.getPlayerAdapter(Player::class.java)?.also { adapter = it }
}

/**
 * The LuckPerms user for a player, read from the platform cache.
 *
 * This is safe to call on the main thread, unlike loading a user from storage.
 */
internal val Player.lpUser: User?
    get() = LuckPermsManager.playerAdapter?.getUser(this)

/**
 * The cached meta data (prefix, suffix, weight, meta) for a player.
 */
internal val Player.lpMetaData: CachedMetaData?
    get() = LuckPermsManager.playerAdapter?.getMetaData(this)

/**
 * The cached permission data for a player.
 */
internal val Player.lpPermissionData: CachedPermissionData?
    get() = LuckPermsManager.playerAdapter?.getPermissionData(this)

/**
 * The query options (contexts and flags) currently active for a player.
 */
internal val Player.lpQueryOptions: QueryOptions
    get() = LuckPermsManager.playerAdapter?.getQueryOptions(this) ?: QueryOptions.nonContextual()

/**
 * The contexts currently active for a player.
 */
internal val Player.lpContexts: ImmutableContextSet
    get() = LuckPermsManager.playerAdapter?.getContext(this) ?: ImmutableContextSet.empty()

/**
 * Get a track by name, if it is loaded.
 */
internal fun getTrack(name: String): Track? =
    LuckPermsManager.luckPerms?.trackManager?.getTrack(name)

/**
 * Read a context set from a list of `key=value` strings.
 */
internal fun Config.getContexts(key: String): ImmutableContextSet {
    val strings = this.getStringsOrNull(key) ?: return ImmutableContextSet.empty()

    if (strings.isEmpty()) {
        return ImmutableContextSet.empty()
    }

    val contexts = MutableContextSet.create()

    for (string in strings) {
        val split = string.split("=", limit = 2)

        if (split.size != 2) {
            continue
        }

        contexts.add(split[0].trim(), split[1].trim())
    }

    return contexts.immutableCopy()
}

/**
 * Read the node expiry from the `duration` argument, in seconds.
 *
 * Returns null for a permanent node.
 */
internal fun Config.getNodeExpiry(data: TriggerData?): Duration? {
    if (!this.has("duration")) {
        return null
    }

    val seconds = this.getIntFromExpression("duration", data)

    return if (seconds <= 0) null else Duration.ofSeconds(seconds.toLong())
}

/**
 * Get the node map to write to, honouring the `transient` argument.
 */
internal fun User.dataFor(config: Config): NodeMap =
    if (config.getBool("transient")) this.transientData() else this.data()

/**
 * Modify a player's LuckPerms user asynchronously, saving the result.
 */
internal fun modifyUser(player: Player, block: (User) -> Unit): Boolean {
    val userManager = LuckPermsManager.luckPerms?.userManager ?: return false

    userManager.modifyUser(player.uniqueId, block)

    return true
}
