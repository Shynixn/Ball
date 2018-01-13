package com.github.shynixn.balls.bukkit.logic.persistence.controller

import com.github.shynixn.balls.api.persistence.controller.IFileController
import com.github.shynixn.balls.bukkit.BallPlugin
import com.github.shynixn.balls.bukkit.logic.persistence.entity.ItemContainer
import org.bukkit.configuration.MemorySection
import org.bukkit.inventory.ItemStack
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
 * SOFTWARE.j
 */
internal class FixedGUIItemConfiguration(private val plugin: Plugin) : IFileController<ItemContainer> {
    private val items: MutableMap<String, ItemContainer> = HashMap()

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    override fun store(item: ItemContainer?) {
        throw RuntimeException("Not supported!")
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    override fun getAll(): MutableList<ItemContainer> {
        return ArrayList(items.values)
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    override fun remove(item: ItemContainer?) {
        throw RuntimeException("Not supported!")
    }

    /**
     * Clears all items in the repository.
     */
    override fun clear() {
        items.clear()
    }

    /**
     * Reloads the content from the fileSystem.
     */
    override fun reload() {
        this.items.clear()
        this.plugin.reloadConfig()
        val data = (this.plugin.config.get("gui.items") as MemorySection).getValues(false)
        for (key in data.keys) {
            try {
                val container = ItemContainer(0, (data[key] as MemorySection).getValues(false))
                this.items.put(key, container)
            } catch (e: Exception) {
                BallPlugin.logger().log(Level.WARNING, "Failed to load guiItem $key.", e)
            }
        }
    }

    /**
     * Returns the guiItem by the given name
     *
     * @param name name
     * @return item
     */
    fun getGUIItemByName(name: String): ItemContainer? {
        return if (this.items.containsKey(name)) this.items[name] else null
    }

    /**
     * Returns if the given itemStack is a guiItemStack with the given name
     *
     * @param itemStack itemStack
     * @param name      name
     * @return itemStack
     */
    fun isGUIItem(itemStack: Any?, name: String?): Boolean {
        if (itemStack == null || name == null)
            return false
        val container = this.getGUIItemByName(name)
        val mItemStack = itemStack as ItemStack?
        return (mItemStack!!.itemMeta != null
                && mItemStack.itemMeta.displayName != null
                && mItemStack.itemMeta.displayName.equals(container?.name, ignoreCase = true))
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    override fun size(): Int {
        return items.size
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        items.clear()
    }
}