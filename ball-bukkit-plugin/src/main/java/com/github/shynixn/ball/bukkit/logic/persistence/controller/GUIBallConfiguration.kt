package com.github.shynixn.ball.bukkit.logic.persistence.controller

import com.github.shynixn.ball.bukkit.BallPlugin
import com.github.shynixn.ball.bukkit.core.logic.persistence.controller.BallDataRepository
import com.github.shynixn.ball.bukkit.logic.persistence.entity.ItemContainer
import org.bukkit.configuration.MemorySection
import org.bukkit.plugin.Plugin
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
class GUIBallConfiguration(private val plugin: Plugin, fileName: String?, private val items: MutableMap<Int, ItemContainer> = HashMap()) : BallDataRepository(plugin, fileName) {


    /**
     * Returns the gui items.
     */
    fun getGUIItems(): List<ItemContainer> {
        return ArrayList(this.items.values)
    }

    /**
     * Reloads the content from the fileSystem.
     */
    override fun reload() {
        super.reload()
        this.items.clear()
        this.plugin.reloadConfig()
        val data = (this.plugin.config.get("meta") as MemorySection).getValues(false)
        for (key in data.keys) {
            try {
                val any = (data[key] as MemorySection).getValues(false).get("gui")
                val container = ItemContainer(0, (any as MemorySection).getValues(false))
                this.items.put(Integer.parseInt(key), container)
            } catch (e: Exception) {
                BallPlugin.logger().log(Level.WARNING, "Failed to load guiItem $key.", e)
            }
        }
    }
}