package com.github.shynixn.balls.bukkit.logic.business.listener

import com.github.shynixn.balls.bukkit.BallPlugin
import com.github.shynixn.balls.bukkit.core.logic.business.listener.SimpleListener
import com.github.shynixn.balls.bukkit.logic.business.gui.GUI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
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
class GUIListener(plugin: Plugin) : SimpleListener(plugin) {

    /**
     * Event which closes the Ball GUI on inventory close.
     */
    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        if (event.inventory.holder is GUI) {
            val gui = event.inventory.holder as GUI
            try {
                gui.close()
            } catch (e: Exception) {
                BallPlugin.logger().log(Level.WARNING, "Failed to close inventory.", e)
            }
        }
    }

    /**
     * Event which handles clicking the the Ball GUI on inventory click.
     */
    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.inventory.holder is GUI) {
            val gui = event.inventory.holder as GUI
            try {
                gui.click(event.currentItem, event.slot)
                event.isCancelled = true
                (event.whoClicked as Player).updateInventory()
            } catch (e: Exception) {
                BallPlugin.logger().log(Level.WARNING, "Failed to click in inventory.", e)
            }
        }
    }
}