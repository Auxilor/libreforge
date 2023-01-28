package com.willfp.libreforge.loader

import com.willfp.eco.core.EcoPlugin
import com.willfp.libreforge.LibreforgeInitializer
import java.io.File
import java.io.FileOutputStream
import java.net.URLClassLoader

private const val CLASS_NAME = "com.willfp.libreforge.LibreforgeInitializer"

fun initLibreforge(plugin: EcoPlugin) {
    try {
        Class.forName(CLASS_NAME)
    } catch (e: ClassNotFoundException) {
        val outDir = File(plugin.dataFolder.parentFile, "libreforge")
        outDir.mkdirs()
        val libreforgeJar = File(outDir, "libreforge.jar")

        val inputStream = plugin::class.java.getResourceAsStream("libreforge.jar")
            ?: throw LibreforgeNotFoundError("libreforge wasn't found in the plugin jar!")

        FileOutputStream(libreforgeJar).use {
            inputStream.copyTo(it)
            inputStream.close()
        }

        Class.forName(
            CLASS_NAME,
            true,
            URLClassLoader(arrayOf(libreforgeJar.toURI().toURL()), plugin::class.java.classLoader)
        )
    }

    LibreforgeInitializer.addPlugin(plugin)
}

private class LibreforgeNotFoundError(
    override val message: String
) : Error(message)
