package com.willfp.libreforge.earning

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.util.NamespacedKeyUtils

class EarningLimitGroup internal constructor(id: String) {
    internal data class Version(
        val policy: EarningPolicy,
        val since: Long
    )

    internal val key by lazy {
        PersistentDataKey(
            NamespacedKeyUtils.createEcoKey("earning_limit_$id"),
            PersistentDataKeyType.STRING,
            ""
        )
    }

    @Volatile
    internal var versions = emptyList<Version>()

    @Volatile
    internal var active = false
}
