package com.willfp.libreforge.integrations.skinsrestorer

import net.skinsrestorer.api.PropertyUtils
import net.skinsrestorer.api.SkinsRestorer
import net.skinsrestorer.api.SkinsRestorerProvider
import net.skinsrestorer.api.event.SkinApplyEvent
import net.skinsrestorer.api.model.MojangProfileResponse
import net.skinsrestorer.api.property.SkinApplier
import net.skinsrestorer.api.property.SkinIdentifier
import net.skinsrestorer.api.property.SkinProperty
import net.skinsrestorer.api.property.SkinVariant
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * The SkinsRestorer API, or null if SkinsRestorer isn't loaded yet.
 */
internal val skinsRestorer: SkinsRestorer?
    get() = runCatching { SkinsRestorerProvider.get() }.getOrNull()

/**
 * The skin applier for bukkit players.
 */
internal val skinApplier: SkinApplier<Player>?
    get() = runCatching { skinsRestorer?.getSkinApplier(Player::class.java) }.getOrNull()

/**
 * The SkinApplyEvent currently being handled on this thread, if any.
 *
 * SkinApplyEvent isn't a bukkit event, so it can't be carried on TriggerData#event.
 * The sr_apply_skin trigger sets this around its dispatch so that effects which need to
 * mutate the event (sr_cancel_skin_apply, sr_set_applied_skin) can reach it.
 */
internal val currentSkinApplyEvent = ThreadLocal<SkinApplyEvent?>()

/**
 * A player's stored skin, as read from SkinsRestorer's player storage.
 */
internal class SkinLookup(
    val identifier: SkinIdentifier?,
    val property: SkinProperty?
)

private val emptyLookup = SkinLookup(null, null)

/*

PlayerStorage reads hit SkinsRestorer's database, and conditions are re-evaluated
for every holder on every update, so the lookup is cached for a short window.

 */

private const val CACHE_TTL_MILLIS = 2000L

private val skinCache = ConcurrentHashMap<UUID, Pair<Long, SkinLookup>>()

/**
 * Get the stored skin of a player, using a short-lived cache.
 */
internal fun lookupSkin(uuid: UUID): SkinLookup {
    val now = System.currentTimeMillis()

    val cached = skinCache[uuid]

    if (cached != null && now - cached.first < CACHE_TTL_MILLIS) {
        return cached.second
    }

    val api = skinsRestorer ?: return emptyLookup

    val lookup = runCatching {
        SkinLookup(
            api.playerStorage.getSkinIdOfPlayer(uuid).orElse(null),
            api.playerStorage.getSkinOfPlayer(uuid).orElse(null)
        )
    }.getOrDefault(emptyLookup)

    skinCache[uuid] = now to lookup

    return lookup
}

/**
 * Get the stored skin of a player, using a short-lived cache.
 */
internal fun Player.lookupSkin() = lookupSkin(this.uniqueId)

/**
 * Invalidate the cached skin of a player.
 */
internal fun invalidateSkinCache(uuid: UUID) {
    skinCache.remove(uuid)
}

/**
 * Parse the mojang profile out of a skin property.
 */
internal fun SkinProperty.profile(): MojangProfileResponse? =
    runCatching { PropertyUtils.getSkinProfileData(this) }.getOrNull()

/**
 * The texture hash of a skin property.
 */
internal fun SkinProperty.textureHash(): String? =
    runCatching { PropertyUtils.getSkinTextureHash(this) }.getOrNull()

/**
 * The full texture URL of a skin property.
 */
internal fun SkinProperty.textureUrl(): String? =
    runCatching { PropertyUtils.getSkinTextureUrl(this) }.getOrNull()

/**
 * The texture URL of a skin property, without the mojang textures prefix.
 */
internal fun SkinProperty.strippedTextureUrl(): String? =
    runCatching { PropertyUtils.getSkinTextureUrlStripped(this) }.getOrNull()

/**
 * The variant (classic or slim) of a skin property.
 */
internal fun SkinProperty.variant(): SkinVariant? =
    runCatching { PropertyUtils.getSkinVariant(this) }.getOrNull()

/**
 * If a skin property has a cape.
 */
internal fun SkinProperty.hasCape(): Boolean =
    profile()?.textures?.getCAPE() != null

/**
 * Parse a skin variant from config, defaulting to classic.
 */
internal fun parseSkinVariant(name: String?): SkinVariant =
    when (name?.uppercase()) {
        "SLIM" -> SkinVariant.SLIM
        else -> SkinVariant.CLASSIC
    }

/**
 * The accepted skin variants, for wiki documentation.
 */
internal val skinVariantChoices = listOf("CLASSIC", "SLIM")

/**
 * The accepted skin types, for wiki documentation.
 */
internal val skinTypeChoices = listOf("PLAYER", "URL", "CUSTOM", "LEGACY")
