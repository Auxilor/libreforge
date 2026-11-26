package com.willfp.libreforge.earning

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.NamespacedKeyUtils
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class EarningLimitGroup internal constructor(internal val id: String) {
    internal val key by lazy {
        PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("earning_limit_$id"),
            PersistentDataKeyType.STRING,
            ""
        )
    }

    internal val players: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    @Volatile
    internal lateinit var policy: EarningPolicy

    @Volatile
    internal var active = false
}
