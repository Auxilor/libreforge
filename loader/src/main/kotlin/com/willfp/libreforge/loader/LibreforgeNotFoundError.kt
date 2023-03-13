package com.willfp.libreforge.loader

class LibreforgeNotFoundError(
    override val message: String
) : Error(message)
