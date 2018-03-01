package com.github.shynixn.ball.bukkit.logic.business.gui

import com.github.shynixn.ball.api.BallApi
import com.github.shynixn.ball.api.persistence.BallMeta
import com.github.shynixn.ball.bukkit.BallPlugin
import com.github.shynixn.ball.bukkit.logic.business.Permission
import com.github.shynixn.ball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.ball.bukkit.logic.business.helper.sendMessage
import com.github.shynixn.ball.bukkit.logic.persistence.BallsManager
import com.github.shynixn.ball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.ball.bukkit.logic.persistence.entity.ItemContainer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

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
internal class GUI(private val player: Player, private val ballManager: BallsManager) : InventoryHolder, AutoCloseable {

    private val suggestHeadMessage = ChatBuilder().text(Config.prefix)
            .text("Click here: ")
            .component(">>Submit skin<<")
            .setColor(ChatColor.YELLOW)
            .setClickAction(ChatBuilder.ClickAction.OPEN_URL, "http://minecraft-heads.com/custom/heads-generator")
            .setHoverText("Goto the Minecraft-Heads website!")
            .builder()
            .text(" ")
            .component(">>Suggest new ball<<")
            .setColor(ChatColor.YELLOW)
            .setClickAction(ChatBuilder.ClickAction.OPEN_URL, "http://minecraft-heads.com/forum/suggesthead")
            .setHoverText("Goto the Minecraft-Heads website!")
            .builder()

    private var playerInventory: Inventory? = null
    private var startCount: Int = 0
    private var currentCount: Int = 0

    init {
        this.inventory
    }

    override fun getInventory(): Inventory {
        if (this.playerInventory != null) {
            return this.playerInventory!!
        }
        if (this.player.openInventory != null) {
            this.player.closeInventory()
        }
        this.playerInventory = Bukkit.createInventory(this, 54, Config.guiTitle)
        this.player.openInventory(this.playerInventory)
        this.setItems(Config.ballItemsController?.getGUIItems()!!, 1, Permission.SINGLEGUIBALL)
        return this.playerInventory!!
    }

    @Throws(Exception::class)
    fun click(clickedItem: ItemStack, slot: Int) {
        var slot = slot
        if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "next-page")!!) {
            this.setItems(Config.ballItemsController?.getGUIItems()!!, 1, Permission.SINGLEGUIBALL)
        } else if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "previous-page")!!) {
            this.setItems(Config.ballItemsController?.getGUIItems()!!, 2, Permission.SINGLEGUIBALL)
        } else if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "back")!!) {
            player.closeInventory()
        } else if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "suggest-heads")!!) {
            player.sendMessage(suggestHeadMessage)
        } else if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "empty-slot")!!) {
            return
        } else if (slot < 45) {
            slot++
            if (Permission.ALLGUIBALLS.hasPermission(this.player) || Permission.SINGLEGUIBALL.hasPermission(this.player, slot.toString())) {
                var realSlot = slot + currentCount - 1;
                val itemContainer = Config.ballItemsController?.all?.get(realSlot) as BallMeta;
                 this.selectBall(itemContainer)
            }
        }
    }

    @Throws(Exception::class)
    private fun selectBall(ballMeta: BallMeta) {
        BallApi.spawnPlayerBall(player.location, player, ballMeta)
        this.player.closeInventory()
    }

    private fun setItems(containers: List<ItemContainer>, type: Int, groupPermission: Permission) {
        if (type == 1 && (this.startCount % 45 != 0 || containers.size == this.startCount)) {
            return
        }
        if (type == 2) {
            if (this.currentCount == 0) {
                return
            }
            this.startCount = this.currentCount - 45
        }
        var count = this.startCount
        if (count < 0)
            count = 0
        this.currentCount = this.startCount
        val inventory = this.inventory
        inventory.clear()
        var i = 0
        var scheduleCounter = 0
        while (i < 45 && i + this.startCount < containers.size) {
            if (inventory.getItem(i) == null || inventory.getItem(i).type == Material.AIR) {

                val slot = i
                val containerSlot = i + this.startCount
                val mountBlock = this.currentCount
                count++
                if (i % 2 == 0) {
                    scheduleCounter++
                }
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(BallPlugin::class.java), {
                    if (this.currentCount == mountBlock) {
                        inventory.setItem(slot, containers[containerSlot].generate(this.player, groupPermission))
                    }
                }, scheduleCounter.toLong())
            }
            i++
        }
        this.startCount = count
        if (!(this.startCount % 45 != 0 || containers.size == this.startCount)) {
            val nextPage = Config.fixedGuiItemsController?.getGUIItemByName("next-page")
            inventory.setItem(nextPage?.position!!, nextPage.generate(this.player))
        }
        if (this.currentCount != 0) {
            val previousPage = Config.fixedGuiItemsController?.getGUIItemByName("previous-page")
            inventory.setItem(previousPage?.position!!, previousPage.generate(this.player))
        }
        this.setCommonItems(inventory)
        this.fillEmptySlots(inventory)
    }


    private fun setCommonItems(inventory: Inventory) {
        val previousPage = Config.fixedGuiItemsController?.getGUIItemByName("previous-page")
        val nextPage = Config.fixedGuiItemsController?.getGUIItemByName("next-page")
        Config.fixedGuiItemsController?.all!!
                .filter { it != previousPage && it != nextPage }
                .forEach {
                    inventory.setItem(it.position, it.generate(this.player))
                }
    }

    /**
     * Fills empty slots in the inventory with the default item.
     *
     * @param inventory inventory
     */
    private fun fillEmptySlots(inventory: Inventory) {
        (0 until inventory.contents.size)
                .filter { inventory.getItem(it) == null || inventory.getItem(it).type == Material.AIR }
                .forEach { inventory.setItem(it, Config.fixedGuiItemsController?.getGUIItemByName("empty-slot")?.generate(this.player)) }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        this.playerInventory = null
    }
}