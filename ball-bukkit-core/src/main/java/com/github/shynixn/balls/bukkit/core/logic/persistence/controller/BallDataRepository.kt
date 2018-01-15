package com.github.shynixn.balls.bukkit.core.logic.persistence.controller

import com.github.shynixn.balls.api.bukkit.persistence.entity.BukkitBallMeta
import com.github.shynixn.balls.api.persistence.BallMeta
import com.github.shynixn.balls.bukkit.core.logic.persistence.entity.BallData
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class BallDataRepository(private val plugin: Plugin, private val path: Path) : GenericController<BukkitBallMeta>() {

    /**
     * Creates the new ballMeta.
     */
    fun create(skin: String): BukkitBallMeta {
        return BallData(skin)
    }

    /**
     * Saves all items asynchronly into the given file path.
     */
    fun persist() {
        val items = this.getAll().toTypedArray<BallMeta<*, *, *>>()
        this.plugin.server.scheduler.runTaskAsynchronously(this.plugin) {
            try {
                synchronized(path) {
                    val file = createFiles()
                    val configuration = YamlConfiguration()
                    configuration.load(file)
                    for (i in items.indices) {
                        val serializable = items[i] as ConfigurationSerializable
                        configuration.set("meta." + (i + 1) + ".ball", serializable.serialize())
                        configuration.save(file)
                    }
                }
            } catch (ex: IOException) {
                this.plugin.logger.log(Level.WARNING, "Failed to save meta.", ex)
            } catch (ex: InvalidConfigurationException) {
                this.plugin.logger.log(Level.WARNING, "Failed to save meta.", ex)
            }
        }
    }

    /**
     * Reloads the saved item from the given file path.
     */
    fun reload() {
        this.items.clear()
        try {
            synchronized(path) {
                val file = createFiles()
                val configuration = YamlConfiguration()
                configuration.load(file)
                val data = (configuration.get("meta") as MemorySection).getValues(false)
                for (key in data.keys) {
                    try {
                        val ballMeta = BallData(((data[key] as MemorySection).get("ball") as MemorySection).getValues(true))
                        this.items.add(ballMeta)
                    } catch (e: Exception) {
                        this.plugin.logger.log(Level.WARNING, "Failed to load meta $key.", e)
                    }

                }
            }
        } catch (ex: IOException) {
            this.plugin.logger.log(Level.WARNING, "Failed to load meta.", ex)
        } catch (ex: InvalidConfigurationException) {
            this.plugin.logger.log(Level.WARNING, "Failed to load meta.", ex)
        }
    }

    private fun createFiles(): File {
        Files.createDirectories(path.parent)
        val file: File = path.toFile()
        if (!file.exists()) {
            Files.createFile(path)
        }
        return file
    }
}