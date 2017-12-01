package com.github.shynixn.balls.bukkit.logic.business.gui;

import com.github.shynixn.balls.api.BallsApi;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.bukkit.BallsPlugin;
import com.github.shynixn.balls.bukkit.logic.business.Permission;
import com.github.shynixn.balls.bukkit.logic.persistence.BallsManager;
import com.github.shynixn.balls.bukkit.logic.persistence.configuration.Config;
import com.github.shynixn.balls.bukkit.logic.persistence.entity.ItemContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Closeable;
import java.util.List;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class GUI implements InventoryHolder, AutoCloseable {

    private final BallsManager ballsManager;
    private int startCount;
    private int currentCount;
    private Inventory inventory;
    private Player player;

    public GUI(Player player, BallsManager ballsManager) {
        this.player = player;
        this.ballsManager = ballsManager;
        this.getInventory();
    }

    @Override
    public Inventory getInventory() {
        if (this.inventory != null) {
            return this.inventory;
        }
        if (this.player.getOpenInventory() != null) {
            this.player.closeInventory();
        }
        final Inventory inventory = Bukkit.createInventory(this, 54, Config.getInstance().getGUITitle());
        this.player.openInventory(inventory);
        final List<ItemContainer> metas = this.ballsManager.getGUIBallMetaController().getAll();
        this.setItems(metas, 1, "balls.gui.balls");
        return inventory;
    }

    public void click(ItemStack clickedItem, int slot) throws Exception {
        if (Config.getInstance().getGUIItemsController().isGUIItem(clickedItem, "next-page")) {
            final List<ItemContainer> metas = this.ballsManager.getGUIBallMetaController().getAll();
            this.setItems(metas, 1, "balls.gui.balls");
        } else if (Config.getInstance().getGUIItemsController().isGUIItem(clickedItem, "previous-page")) {
            final List<ItemContainer> metas = this.ballsManager.getGUIBallMetaController().getAll();
            this.setItems(metas, 2, "balls.gui.balls");
        } else if (slot < 45) {
            if (Permission.ALLGUIBALLS.hasPermission(this.player) || this.player.hasPermission(Permission.SINGLEGUIBALL.get() + slot)) {
                final ItemContainer itemContainer = this.ballsManager.getGUIBallMetaController().getContainerFromPosition(slot);
                this.selectBall(itemContainer);
            }
        }
    }

    private void selectBall(ItemContainer container) throws Exception {
        final BallMeta ballMeta = this.ballsManager.getGUIBallMetaController().getBallMetaFromContainer(container);
        BallsApi.spawnPlayerBall(this.player.getLocation(), this.player, ballMeta);
        this.close();
    }

    private void setItems(List<ItemContainer> containers, int type, String groupPermission) {
        if (type == 1 && (this.startCount % 45 != 0 || containers.size() == this.startCount)) {
            return;
        }
        if (type == 2) {
            if (this.currentCount == 0) {
                return;
            }
            this.startCount = this.currentCount - 45;
        }

        int count = this.startCount;
        if (count < 0)
            count = 0;
        this.currentCount = this.startCount;
        final Inventory inventory = this.getInventory();
        inventory.clear();
        int i;
        int scheduleCounter = 0;
        for (i = 0; i < 45 && (i + this.startCount) < containers.size(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {

                final int slot = i;
                final int containerSlot = (i + this.startCount);
                final int mountBlock = this.currentCount;
                count++;
                if (i % 2 == 0) {
                    scheduleCounter++;
                }
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(BallsPlugin.class), () -> {
                    if (this.currentCount == mountBlock) {
                        inventory.setItem(slot, containers.get(containerSlot).generate(this.player, groupPermission));
                    }
                }, scheduleCounter);
            }
        }
        this.startCount = count;
        final ItemContainer backGuiItemContainer = Config.getInstance().getGUIItemsController().getGUIItemByName("back");
        inventory.setItem(backGuiItemContainer.getPosition(), backGuiItemContainer.generate(this.player));
        if (!(this.startCount % 45 != 0 || containers.size() == this.startCount)) {
            final ItemContainer nextPage = Config.getInstance().getGUIItemsController().getGUIItemByName("next-page");
            inventory.setItem(nextPage.getPosition(), nextPage.generate(this.player));
        }
        if (this.currentCount != 0) {
            final ItemContainer previousPage = Config.getInstance().getGUIItemsController().getGUIItemByName("previous-page");
            inventory.setItem(previousPage.getPosition(), previousPage.generate(this.player));
        }
        this.fillEmptySlots(inventory);
    }

    /**
     * Fills empty slots in the inventory with the default item
     *
     * @param inventory inventory
     */
    private void fillEmptySlots(Inventory inventory) {
        for (int i = 0; i < inventory.getContents().length; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, Config.getInstance().getGUIItemsController().getGUIItemByName("empty-slot").generate(this.player));
            }
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.player.closeInventory();
        this.inventory = null;
        this.player = null;
    }
}
