package com.github.shynixn.balls.bukkit.logic.business.gui

import com.github.shynixn.balls.api.BallsApi
import com.github.shynixn.balls.bukkit.BallPlugin
import com.github.shynixn.balls.bukkit.logic.business.Permission
import com.github.shynixn.balls.bukkit.logic.persistence.BallsManager
import com.github.shynixn.balls.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.balls.bukkit.logic.persistence.entity.ItemContainer
import org.bukkit.Bukkit
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

    private var playerInventory : Inventory? = null
    private var startCount : Int = 0
    private var currentCount : Int = 0

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
    //    val metas = ArrayList<ItemContainer>();
     //   this.setItems(metas, 1, Permission.SINGLEGUIBALL)
        return this.playerInventory!!
    }

    @Throws(Exception::class)
    fun click(clickedItem: ItemStack, slot: Int) {
        var slot = slot
       /* if (Config.fixedGuiItemsController?.isGUIItem(clickedItem, "next-page")) {
            val metas = this.ballManager.fileRepository.getAll()
     //       this.setItems(metas, 1, Permission.SINGLEGUIBALL)
        } else if (Config.fixedGuiItemsController.isGUIItem(clickedItem, "previous-page")) {
            val metas = this.ballManager.fileRepository.getAll()
      //      this.setItems(metas, 2, Permission.SINGLEGUIBALL)
        } else if (Config.fixedGuiItemsController.isGUIItem(clickedItem, "empty-slot")) {
            return
        } else if (slot < 45) {
            slot++
            if (Permission.ALLGUIBALLS.hasPermission(this.player) || Permission.SINGLEGUIBALL.hasPermission(this.player, slot.toString())) {
           //     val itemContainer = this.ballManager.fileRepository.getContainerFromPosition(slot)
                this.selectBall(itemContainer)
            }
        }*/
    }

    @Throws(Exception::class)
    private fun selectBall(container: ItemContainer) {
     //   val ballMeta = this.ballManager.fileRepository.getBallMetaFromContainer(container)
    //    BallsApi.spawnPlayerBall(this.player.location, this.player, ballMeta)
        this.close()
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

                val containerSlot = i + this.startCount
                val mountBlock = this.currentCount
                count++
                if (i % 2 == 0) {
                    scheduleCounter++
                }
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(BallPlugin::class.java), {
                    if (this.currentCount == mountBlock) {
                        inventory.setItem(i, containers[containerSlot].generate(this.player, groupPermission))
                    }
                }, scheduleCounter.toLong())
            }
            i++
        }
        this.startCount = count
        if (!(this.startCount % 45 != 0 || containers.size == this.startCount)) {
        //    val nextPage = Config.fixedGuiItemsController.getGUIItemByName("next-page")
        //    inventory.setItem(nextPage.getPosition(), nextPage.generate(this.player))
        }
        if (this.currentCount != 0) {
        //    val previousPage = Config.fixedGuiItemsController.getGUIItemByName("previous-page")
       //     inventory.setItem(previousPage.getPosition(), previousPage.generate(this.player))
        }
        this.fillEmptySlots(inventory)
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
        this.player.closeInventory()
        this.playerInventory = null
    }
}