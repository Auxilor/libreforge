package com.willfp.libreforge.loader.internal.configs

import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import java.io.FileOutputStream

internal class FoundConfig(
    name: String,
    category: ConfigCategory,
    private val plugin: LibreforgePlugin
) {
    private val source = plugin::class.java.classLoader
    private val resourcePath = "${category.directory}/$name.yml"

    fun copy() {
        val inputStream = source.getResourceAsStream(resourcePath) ?: return
        val outFile = plugin.dataFolder.resolve(resourcePath)

        if (!outFile.exists()) {
            outFile.parentFile.mkdirs()
            FileOutputStream(outFile).use { outStream ->
                inputStream.copyTo(outStream)
            }
        }

        inputStream.close()
    }
}
