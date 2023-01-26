package com.willfp.libreforge

import com.willfp.eco.core.config.interfaces.Config

interface ConfiguredProperty<T> {
    val config: Config
    val compileData: T?
}
