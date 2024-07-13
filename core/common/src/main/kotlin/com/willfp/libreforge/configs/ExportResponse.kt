package com.willfp.libreforge.configs

import com.willfp.eco.core.config.interfaces.Config

data class ExportResponse(
    val success: Boolean,
    val code: Int,
    val body: Config
)
