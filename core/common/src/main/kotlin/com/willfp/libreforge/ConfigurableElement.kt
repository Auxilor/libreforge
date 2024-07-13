package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config
import java.util.UUID

interface ConfigurableElement {
    /**
     * The uuid of the effect.
     */
    val uuid: UUID

    /**
     * The config of the effect.
     */
    val config: Config
}
